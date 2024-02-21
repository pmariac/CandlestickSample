package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun App() {
    MaterialTheme {
		Column(Modifier.fillMaxSize()) {
			Spacer(Modifier.statusBarsPadding())

			DualCharts(Modifier.fillMaxSize())

			Spacer(Modifier.navigationBarsPadding())
		}
    }
}
