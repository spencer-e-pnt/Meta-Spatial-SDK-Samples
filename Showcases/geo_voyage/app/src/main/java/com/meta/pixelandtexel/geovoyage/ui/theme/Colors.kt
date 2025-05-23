// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.theme

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.meta.spatial.uiset.theme.lightSpatialColorScheme

object GeoVoyageColors {
    val navContainer1 = Color(0xFFEBF5E9)
    val navContainer2 = Color(0xFFd2dbd0)
    val textContainer1 = Color(0xFFB2F0B8)
    val textContainer2 = Color(0xFF9dd4a3)
    val button = Color(0xFF29666E)
    val navIcons = Color(0xFF001F24)
    val navSelected = Color(0xFFBDEAF4)
    val settingsCog = Color(0xFF727970)
    val textColor = Color(0xFF423F47)
    val errorColor = Color(0xFFB91A1A)
}

val geoVoyageColorScheme = lightSpatialColorScheme().copy(
    primaryButton = GeoVoyageColors.button,
    secondaryButton = Color(0xFFECEFE8),
    panel =
        Brush.verticalGradient(
            colors =
                listOf(
                    GeoVoyageColors.navContainer1,
                    GeoVoyageColors.navContainer2,
                ),
        ),
    dialog =
        Brush.verticalGradient(
            colors =
                listOf(
                    GeoVoyageColors.textContainer1,
                    GeoVoyageColors.textContainer2,
                ),
        ),
)
