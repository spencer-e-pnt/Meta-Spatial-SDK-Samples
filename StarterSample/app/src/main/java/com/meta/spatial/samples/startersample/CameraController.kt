package com.meta.spatial.samples.startersample

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CameraController(
    private val context: Context,
    private val cameraEye: CameraEye = CameraEye.LEFT,
) {
    companion object {
        private const val TAG = "Camera"

        private const val CAMERA_IMAGE_FORMAT = ImageFormat.YUV_420_888
        private const val CAMERA_SOURCE_KEY = "com.meta.extra_metadata.camera_source"
        private const val CAMERA_POSITION_KEY = "com.meta.extra_metadata.position"
    }

    private var _isRunning = AtomicBoolean(false)
    val isRunning: Boolean get() = _isRunning.get()

    private lateinit var cameraId: String
    private lateinit var cameraCharacteristics: CameraCharacteristics

    val isInitialized: Boolean get() = ::cameraId.isInitialized

    private lateinit var cameraManager: CameraManager

    // threads for camera handling and image frame acquisition
    private val cameraThread = HandlerThread("CameraThread").apply { start() }
    private val cameraHandler = Handler(cameraThread.looper)
    private val imageReaderThread = HandlerThread("ImageReaderThread").apply { start() }
    private val imageReaderHandler = Handler(imageReaderThread.looper)

    private var camera: CameraDevice? = null
    private var session: CameraCaptureSession? = null

    // our frame ready for object detection
    private lateinit var imageReader: ImageReader
    private val isProcessingFrame = AtomicBoolean(false)

    fun initialize() {
        Log.d(TAG, "initialize")

        cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager

        Log.d(TAG, "Found camera ids: ${cameraManager.cameraIdList.joinToString()}")

        if (cameraManager.cameraIdList.isEmpty()) {
            throw RuntimeException("Failed to get system camera")
        }

        // get our camera characteristics

        val cameraEyeIds = HashMap<CameraEye, String>()
        val cameraEyeCharacteristics = HashMap<CameraEye, CameraCharacteristics>()
        for(id in cameraManager.cameraIdList) {
            val characteristics = cameraManager.getCameraCharacteristics(id)

            val position =
                characteristics.get(CameraCharacteristics.Key(CAMERA_POSITION_KEY, Int::class.java))
            val eye = when (position) {
                0 -> CameraEye.LEFT
                1 -> CameraEye.RIGHT
                else -> CameraEye.UNKNOWN
            }

            // get store the characteristics of each camera

            cameraEyeIds[eye] = id
            cameraEyeCharacteristics[eye] = characteristics
        }

        if (cameraEyeIds[cameraEye] == null) {
            throw RuntimeException("Failed to get camera for ${cameraEye.name} eye")
        }

        cameraId = cameraEyeIds[cameraEye]!!
        cameraCharacteristics = cameraEyeCharacteristics[cameraEye]!!
    }

    fun start(
        imageFormat: Int = CAMERA_IMAGE_FORMAT,
        surfaces: List<Surface> = listOf(),
        imageAvailableListener: ImageAvailableListener? = null
    ) {
        Log.d(TAG, "start")

        if (!isInitialized) {
            throw RuntimeException("Camera not initialized")
        }

        if (_isRunning.get()) {
            Log.w(TAG, "Camera controller already running")
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            startInternal(imageFormat, surfaces, imageAvailableListener)
        }
    }

    private suspend fun startInternal(
        imageFormat: Int,
        surfaces: List<Surface>,
        imageAvailableListener: ImageAvailableListener? = null
    ) {
        Log.d(TAG, "startInternal")

        val formats =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .outputFormats

        // output formats: 256 (JPEG), 34 (PRIVATE), 35 (YUV_420_888)
        Log.d(TAG, "Found camera output formats: ${formats.joinToString()}")

        if (!formats.contains(imageFormat)) {
            throw RuntimeException("Image format $imageFormat unsupported")
        }

        val sizes =
            cameraCharacteristics.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
                .getOutputSizes(imageFormat)
        // output sizes: 320x240, 640x480, 800x600, 1280x960
        Log.d(TAG, "Found camera output sizes: ${sizes.joinToString()}")

        // just use the largest resolution for now
        val cameraOutputSize = sizes.maxBy { it.width * it.height }!!

        Log.d(TAG, "Using camera format $imageFormat and size $cameraOutputSize")

        try {
            _isRunning.set(true)

            // open our device camera
            camera = openCamera(cameraManager, cameraId, cameraHandler)

            // initialize our image ready for frame object detection
            imageReader = ImageReader.newInstance(
                cameraOutputSize.width, cameraOutputSize.height, imageFormat, 2
            )

            // create and start our session with the open camera and list of target surfaces
            val targets = surfaces.toMutableList()
            targets.add(imageReader.surface)
            session = createCameraPreviewSession(camera!!, targets, cameraHandler)

            // set our repeating capture request
            val captureRequestBuilder =
                camera!!.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    targets.forEach { addTarget(it) }
                }
            session!!.setRepeatingRequest(captureRequestBuilder.build(), null, cameraHandler)

            // setup our image reader to receive frames
            imageReader.setOnImageAvailableListener({ reader ->
                if (isProcessingFrame.get()) {
                    // still processing our last image
                    return@setOnImageAvailableListener
                }

                val image = reader.acquireLatestImage() ?: return@setOnImageAvailableListener

                //Log.d(TAG, "Image available: ${image.format}, ${image.width}x${image.height}")

                try {
                    isProcessingFrame.set(true)

                    // do any heavy image processing, like object detection
                    imageAvailableListener?.onNewImage(image, image.width, image.height)
                } finally {
                    image.close()
                    isProcessingFrame.set(false)
                }
            }, imageReaderHandler)
        } catch (e: Exception) {
            e.printStackTrace()
            stop()
        }
    }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCoroutine { cont ->
        Log.d(TAG, "openCamera $cameraId")

        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(camera: CameraDevice) {
                Log.d(TAG, "camera onOpened")

                cont.resume(camera)
            }

            override fun onDisconnected(camera: CameraDevice) {
                Log.d(TAG, "camera onDisconnected")

                this@CameraController.stop()
            }

            override fun onError(camera: CameraDevice, error: Int) {
                Log.d(TAG, "camera onError")

                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val ex = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, ex.message, ex)

                cont.resumeWithException(ex)
            }
        }, handler)
    }

    private suspend fun createCameraPreviewSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->
        Log.d(TAG, "createCameraPreviewSession")

        device.createCaptureSession(
            targets,
            object : CameraCaptureSession.StateCallback() {
                override fun onConfigured(session: CameraCaptureSession) {
                    Log.d(TAG, "configured camera capture session")

                    cont.resume(session)
                }

                override fun onConfigureFailed(session: CameraCaptureSession) {
                    Log.e(TAG, "camera capture session configure failed")
                    val exc = RuntimeException("Camera ${device.id} session configuration failed")
                    Log.e(TAG, exc.message, exc)

                    cont.resumeWithException(exc)
                }
            }, handler
        )
    }

    fun stop() {
        Log.d(TAG, "stop")

        _isRunning.set(false)

        session?.close()
        session = null

        camera?.close()
        camera = null
    }

    fun dispose() {
        Log.d(TAG, "dispose")
        stop()

        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }
}