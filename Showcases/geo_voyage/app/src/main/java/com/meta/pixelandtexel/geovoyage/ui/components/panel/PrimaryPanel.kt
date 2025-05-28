// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes

/**
 * Primary GeoVoyage panel to display content.
 *
 * @param content Content to display within the [PrimaryPanel]
 */
@Composable
fun PrimaryPanel(
    content: @Composable () -> Unit,
) {
  Box(
      modifier = Modifier
          .clip(LocalShapes.current.large)
          .background(
              brush = LocalColorScheme.current.panel,
              shape = LocalShapes.current.large
          )
  ) {
    Column { content.invoke() }
  }
}

@Preview(widthDp = 800, heightDp = 600)
@Composable
fun PreviewGeoVoyagePrimaryPanel() {
  GeoVoyageTheme { PrimaryPanel {} }
}
