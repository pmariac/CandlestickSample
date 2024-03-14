package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import kotlinx.datetime.Instant

/**
 * Just a simple candlestick chart, no volume chart, no size & events synchronization.
 */
@Composable
fun SimpleChart(
	modifier: Modifier = Modifier
) {

	val from = Instant.parse("2023-03-12T16:00:00.000Z")

	// Generate a random dataset of 200 samples
	val dataset = generateDataset(from, 200)
	Column(modifier) {
		Viz(modifier = Modifier.fillMaxSize()) {
			it.candleStick(dataset, 40)
		}
	}
}
