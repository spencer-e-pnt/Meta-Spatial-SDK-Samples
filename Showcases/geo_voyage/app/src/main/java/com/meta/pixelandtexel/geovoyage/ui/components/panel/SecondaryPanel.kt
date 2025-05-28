// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.ui.components.panel

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.meta.pixelandtexel.geovoyage.ui.theme.GeoVoyageTheme
import com.meta.spatial.uiset.theme.LocalColorScheme
import com.meta.spatial.uiset.theme.LocalShapes

/**
 * Secondary GeoVoyage panel to display content. To be used within Primary GeoVoyage Panel.
 *
 * @param content Content to display within the [SecondaryPanel]
 */
@Composable
fun SecondaryPanel(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
  Box(
      modifier = modifier
          .shadow(
            elevation = 16.dp,
            shape = LocalShapes.current.small,
          )
          .background(
              brush = LocalColorScheme.current.dialog,
              shape = LocalShapes.current.small
          )
          .clip(LocalShapes.current.small)
  ) {
    Box(modifier = Modifier.padding(26.dp)) { content.invoke() }
  }
}

@Preview(
    widthDp = 932,
    heightDp = 650,
)
@Composable
fun SecondaryPanelPreview() {
    GeoVoyageTheme {
        SecondaryPanel {  }
    }
}