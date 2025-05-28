// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.buttons

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
fun PrimaryButton(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = LocalColorScheme.current.primaryButton,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
  Button(
      onClick = onClick,
      colors =
          ButtonDefaults.buttonColors(
              containerColor = color, // Background color of the button
              contentColor = Color.White, // Text color of the button
              disabledContainerColor = Color.Gray, // Background color when the button is disabled
              disabledContentColor = Color.LightGray // Text color when the button is disabled
          ),
      modifier =
          modifier
              .shadow(
                  elevation = 20.dp,
                  shape = RoundedCornerShape(size = 50.dp))
              .width(350.dp)
              .height(100.dp)
              .background(color = color, shape = RoundedCornerShape(size = 50.dp)),
      enabled = enabled) {
        Text(text = text, style = LocalTypography.current.headline2.copy(color = Color.White))
      }
}

@Preview
@Composable
private fun PreviewPrimaryButton() {
  GeoVoyageTheme { PrimaryButton(text = "Primary Button", onClick = {}) }
}
