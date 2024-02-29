package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Just a simple candlestick chart, no volume chart, no size & events synchronization.
 */
@Composable
fun SimpleChart(
	modifier: Modifier = Modifier
) {

	// Generate a random dataset of 200 samples
	val dataset = generateDataset(200)
	Column(modifier) {
		Viz(modifier = Modifier.fillMaxSize()) {
			it.candleStick(dataset, 40)
		}
	}
}
