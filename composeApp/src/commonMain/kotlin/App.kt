package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun App() {
    MaterialTheme {
		Column(Modifier.fillMaxSize().systemBarsPadding()) {
			DualCharts(Modifier.fillMaxSize())
			//SimpleChart(Modifier.fillMaxSize())
		}
    }
}
