package io.data2viz.sample

import androidx.compose.foundation.layout.Column
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
 * Dual chart view, on the top the candlestick chart, on the bottom the volume histogram.
 *
 * The 2 charts are synchronized (zoom, pan, selection...), but only the top one can manage
 * user events.
 *
 * Please note that it is a WORK IN PROGRESS, and the "resize" is not currently handled due to
 * Compose + Synchronizer, we are working on it.
 */
@Composable
fun DualCharts(
	modifier: Modifier = Modifier
) {

	// Storing the 2 charts
	var priceChart: Chart<PriceMovement>? by remember { mutableStateOf(null) }
	var volumeChart: Chart<PriceMovement>? by remember { mutableStateOf(null) }

	// Storing the charts' sizes
	var priceChartSize: Size by remember { mutableStateOf(Size(.0, .0)) }
	var volumeChartSize: Size by remember { mutableStateOf(Size(.0, .0)) }

	// This may be removed when the "resizing issue" will be resolved
	synchronizeEffect(
		firstChart = priceChart,
		secondChart = volumeChart,
		firstChartSize = priceChartSize,
		secondChartSize = volumeChartSize
	)

	// Generate a random dataset of 40 samples
	val dataset = generateDataset(40)

	// Default layout : 80% top is the candlestick, 20% bottom is the volume chart.
	Column(modifier) {
		Viz(modifier = Modifier.fillMaxWidth().weight(.8f)) {
			it.keepSizeForSynchronization { newSize -> priceChartSize = newSize }
			priceChart = it.candleStick(dataset)
		}
		Viz(modifier = Modifier.fillMaxWidth().weight(.2f)) {
			it.keepSizeForSynchronization { newSize -> volumeChartSize = newSize }
			volumeChart = it.volumeHistogram(dataset)
		}
	}
}

private fun VizContainer.keepSizeForSynchronization(newSize: (Size) -> Unit) {
	newViz { onResize { w, h -> newSize(Size(w, h)) } }
}

@Composable
private fun synchronizeEffect(
	firstChart: Chart<PriceMovement>?,
	secondChart: Chart<PriceMovement>?,
	firstChartSize: Size,
	secondChartSize: Size
) {
	val sizeManager = remember { sizeManager() }
	val verticalSync = remember { sizeManager.vSynchro() }

	@Suppress("NAME_SHADOWING")
	DisposableEffect(firstChart, secondChart) {
		val firstChart = firstChart ?: return@DisposableEffect onDispose { }
		val secondChart = secondChart ?: return@DisposableEffect onDispose { }
		verticalSync.addAllCharts(firstChart, secondChart)

		firstChart.onZoom { secondChart.zoom(it.zoomAction) }
		firstChart.onViewReset { secondChart.viewReset() }
		firstChart.onHighlight { event ->
			secondChart.highlight(event.data)
			event.selectedData.firstOrNull()?.let { secondChart.setCursorFor(it) }
		}
		onDispose {
			// should remove listeners
		}
	}

	LaunchedEffect(firstChartSize, secondChartSize) {
		sizeManager.synchronize()
	}
}
