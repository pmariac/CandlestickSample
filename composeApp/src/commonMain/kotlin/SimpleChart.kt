package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.mark.domainSpecific.PriceMovement
import io.data2viz.charts.layout.sizeManager
import io.data2viz.geom.Size
import io.data2viz.viz.VizContainer

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
