// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import com.meta.pixelandtexel.geovoyage.R
import com.meta.spatial.uiset.theme.SpatialTypography

val provider =
    GoogleFont.Provider(
        providerAuthority = "com.google.android.gms.fonts",
        providerPackage = "com.google.android.gms",
        certificates = R.array.com_google_android_gms_fonts_certs
    )

val montserratFontFamily =
    FontFamily(
        Font(
            googleFont = GoogleFont("Montserrat"),
            fontProvider = provider,
        )
    )

val baseline = SpatialTypography()

val geoVoyageTypography = SpatialTypography(
    headline1Strong = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    headline1 = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    headline2Strong = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    headline2 = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    headline3Strong = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    headline3 = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    body1Strong = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    body1 = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    body2Strong = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
    body2 = baseline.headline1Strong.copy(
        fontFamily = montserratFontFamily,
        color = GeoVoyageColors.textColor
    ),
)
