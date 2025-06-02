// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.R
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageColors
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalShapes
import com.meta.spatial.uiset.theme.LocalTypography
import com.meta.spatial.uiset.theme.icons.SpatialIcons
import com.meta.spatial.uiset.theme.icons.regular.World

class NavButtonState(
    val text: String,
    val route: String,
    val iconImage : ImageVector
)

@Composable
fun NavButton(state: NavButtonState, selected: Boolean = false, onClick: () -> Unit) {
  Column(
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center,
      modifier = Modifier.size(130.dp, 130.dp).clickable { onClick() }) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier =
                Modifier.width(120.dp)
                    .height(70.dp)
                    .background(
                        if (selected) GeoVoyageColors.navSelected else Color.Transparent,
                        LocalShapes.current.large)) {
              Icon(
                  imageVector = state.iconImage,
                  contentDescription = null,
                  modifier = Modifier.size(45.dp),
                  tint = GeoVoyageColors.navIcons)
            }
        Text(
            text = state.text,
            style = LocalTypography.current.headline3,
            modifier = Modifier.padding(top = 12.dp))
      }
}

@Preview(showBackground = true)
@Composable
fun PreviewUnselectedGeoVoyageNavButton() {
  val navButtonState =
      NavButtonState(
          text = "GeoVoyage",
          route = "Route",
          iconImage = SpatialIcons.Regular.World,
      )
  GeoVoyageTheme {
    NavButton(navButtonState) {}
  }
}

@Preview(showBackground = true)
@Composable
fun PreviewSelectedGeoVoyageNavButton() {
  val navButtonState =
      NavButtonState(text = "GeoVoyage",
        route = "Route",
        iconImage = SpatialIcons.Regular.World)
  GeoVoyageTheme {
    NavButton(navButtonState, true) {}
  }
}
