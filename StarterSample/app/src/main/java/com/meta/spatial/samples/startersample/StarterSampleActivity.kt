/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.startersample

import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.media.Image
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.meta.spatial.castinputforward.CastInputForwardFeature
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.SpatialFeature
import com.meta.spatial.core.Vector3
import com.meta.spatial.datamodelinspector.DataModelInspectorFeature
import com.meta.spatial.debugtools.HotReloadFeature
import com.meta.spatial.okhttp3.OkHttpAssetFetcher
import com.meta.spatial.ovrmetrics.OVRMetricsDataModel
import com.meta.spatial.ovrmetrics.OVRMetricsFeature
import com.meta.spatial.runtime.LayerConfig
import com.meta.spatial.runtime.NetworkedAssetLoader
import com.meta.spatial.runtime.ReferenceSpace
import com.meta.spatial.runtime.SceneMaterial
import com.meta.spatial.runtime.panel.style
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.Material
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.MeshCollision
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.vr.VRFeature
import java.io.File
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

// default activity
class StarterSampleActivity : AppSystemActivity(), ActivityCompat.OnRequestPermissionsResultCallback,
    ImageAvailableListener
{
  private var gltfxEntity: Entity? = null
  private val activityScope = CoroutineScope(Dispatchers.Main)

    private val cameraPermissions = arrayOf("horizonos.permission.HEADSET_CAMERA")
    private val cameraPermissionsCode = 1000
    private lateinit var permissionsResultCallback: (granted: Boolean) -> Unit
    private lateinit var cameraController: CameraController

  override fun registerFeatures(): List<SpatialFeature> {
    val features = mutableListOf<SpatialFeature>(VRFeature(this))
    if (BuildConfig.DEBUG) {
      features.add(CastInputForwardFeature(this))
      features.add(HotReloadFeature(this))
      features.add(OVRMetricsFeature(this, OVRMetricsDataModel() { numberOfMeshes() }))
      features.add(DataModelInspectorFeature(spatial, this.componentManager))
    }
    return features
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    NetworkedAssetLoader.init(
        File(applicationContext.getCacheDir().canonicalPath), OkHttpAssetFetcher())

      cameraController = CameraController(this)

    // wait for GLXF to load before accessing nodes inside it
    loadGLXF().invokeOnCompletion {
      // get the environment mesh from Cosmo and set it to use an unlit shader.
      val composition = glXFManager.getGLXFInfo("example_key_name")
      val environmentEntity: Entity? = composition.getNodeByName("Environment").entity
      val environmentMesh = environmentEntity?.getComponent<Mesh>()
      environmentMesh?.defaultShaderOverride = SceneMaterial.UNLIT_SHADER
      environmentEntity?.setComponent(environmentMesh!!)
    }
  }

  override fun onSceneReady() {
    super.onSceneReady()

    // set the reference space to enable recentering
    scene.setReferenceSpace(ReferenceSpace.LOCAL_FLOOR)

    scene.setLightingEnvironment(
        ambientColor = Vector3(0f),
        sunColor = Vector3(7.0f, 7.0f, 7.0f),
        sunDirection = -Vector3(1.0f, 3.0f, -2.0f),
        environmentIntensity = 0.3f)
    scene.updateIBLEnvironment("environment.env")

    scene.setViewOrigin(0.0f, 0.0f, 2.0f, 180.0f)

    Entity.create(
        listOf(
            Mesh(Uri.parse("mesh://skybox"), hittable = MeshCollision.NoCollision),
            Material().apply {
              baseTextureAndroidResourceId = R.drawable.skydome
              unlit = true // Prevent scene lighting from affecting the skybox
            },
            Transform(Pose(Vector3(x = 0f, y = 0f, z = 0f)))))
  }

  override fun registerPanels(): List<PanelRegistration> {
    return listOf(
        PanelRegistration(R.layout.ui_example) {
          config {
            themeResourceId = R.style.PanelAppThemeTransparent
            includeGlass = false
            width = 2.0f
            height = 1.5f
            layerConfig = LayerConfig()
            enableTransparent = true
          }
            panel {
                val okButton = rootView?.findViewById<Button>(R.id.ok_button)
                okButton?.setOnClickListener {
                    // stop the camera if it's running

                    if(cameraController.isRunning) {
                        stopCamera()
                        return@setOnClickListener
                    }

                    // first request permissions if we haven't already

                    if(!hasPermissions()) {
                        requestPermissions { granted ->
                            if(granted) {
                                startCamera()
                            }
                        }

                        return@setOnClickListener
                    }

                    // otherwise, start the camera

                    startCamera()
                }
            }
        })
  }

  private fun loadGLXF(): Job {
    gltfxEntity = Entity.create()
    return activityScope.launch {
      glXFManager.inflateGLXF(
          Uri.parse("apk:///scenes/Composition.glxf"),
          rootEntity = gltfxEntity!!,
          keyName = "example_key_name")
    }
  }

    // lifecycle

    override fun onPause() {
        stopCamera()
        super.onPause()
    }

    override fun onDestroy() {
        cameraController.dispose()
        super.onDestroy()
    }

    // camera control

    private fun startCamera() {
        if(!cameraController.isInitialized) {
            cameraController.initialize()
        }

        // the crash bug occurs if using the JPEG image format, but not when using YUV_420_888
        cameraController.start(ImageFormat.JPEG, listOf(), this)
    }

    private fun stopCamera() {
        if(cameraController.isRunning) {
            cameraController.stop()
        }
    }

    override fun onNewImage(image: Image, width: Int, height: Int) {
        // TODO process image, object detection
    }

    // permissions requesting

    private fun hasPermissions() = cameraPermissions.all {
        ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions(callback: (granted: Boolean) -> Unit) {
        permissionsResultCallback = callback

        ActivityCompat.requestPermissions(this, cameraPermissions, cameraPermissionsCode)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        when (requestCode) {
            cameraPermissionsCode -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    permissionsResultCallback(true)
                } else {
                    permissionsResultCallback(false)
                }
            }
        }
    }
}
