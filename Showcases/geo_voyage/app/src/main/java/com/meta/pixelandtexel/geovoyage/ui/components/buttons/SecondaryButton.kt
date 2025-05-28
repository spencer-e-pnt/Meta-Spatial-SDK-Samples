// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalTypography

@Composable
fun SecondaryButton(
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
  OutlinedButton(
      onClick = onClick,
      border = BorderStroke(3.dp, LocalColorScheme.current.primaryButton),
      modifier =
          modifier
              .shadow(
                  elevation = 20.dp,
                  shape = RoundedCornerShape(size = 50.dp)
              )
              .width(350.dp)
              .height(100.dp)
              .background(
                  color = Color(0xFFEBF5E9),
                  shape = RoundedCornerShape(size = 50.dp)),
      enabled = enabled) {
      Text(
          text = text,
          color = LocalColorScheme.current.primaryButton,
          style = LocalTypography.current.headline2
      )
  }
}

@Preview
@Composable
private fun PreviewSecondaryButton() {
  GeoVoyageTheme { SecondaryButton(text = "Secondary Button") {} }
}
