package io.data2viz.sample

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import io.data2viz.viz.VizContainer

@Composable
expect fun Viz(
    modifier: Modifier = Modifier,
    block: (VizContainer) -> Unit
)
