package io.data2viz.sample

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.mark.domainSpecific.PriceMovement
import io.data2viz.charts.layout.sizeManager
import kotlinx.datetime.Instant

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
fun DualCharts(modifier: Modifier = Modifier) {

	// Storing the 2 charts
	val priceChartState: MutableState<Chart<PriceMovement>?> = remember { mutableStateOf(null) }
	val volumeChartState: MutableState<Chart<PriceMovement>?> = remember { mutableStateOf(null) }

	// This may be removed when the "resizing issue" will be resolved
	SynchronizeEffect(
		firstChartState = priceChartState,
		secondChartState = volumeChartState
	)

	val from = Instant.parse("2023-03-12T16:00:00.000Z")
	var dataset = remember { generateDataset(from, 40) }

	// Default layout : 80% top is the candlestick, 20% bottom is the volume chart.
	Column(modifier) {
		TextButton(
			onClick = {
				dataset = (dataset.toMutableList() + generateDataset(dataset.last().timestamp, 1)).toList()
				adjustXAxis(dataset)
				priceChartState.value?.update(dataset)
				volumeChartState.value?.update(dataset)
			},
			content = {
				Text("Update")
			})
		Viz(modifier = Modifier.fillMaxWidth().weight(.8f)) {
			priceChartState.value = it.candleStick(dataset, 40)
		}
		Viz(modifier = Modifier.fillMaxWidth().weight(.2f)) {
			volumeChartState.value = it.volumeHistogram(dataset, 40)
		}
	}
}

@Composable
private fun SynchronizeEffect(
	firstChartState: State<Chart<PriceMovement>?>,
	secondChartState: State<Chart<PriceMovement>?>
) {
	val sizeManager = remember { sizeManager() }
	val verticalSync = remember { sizeManager.vSynchro() }

	val firstChart by firstChartState
	val secondChart by secondChartState

	@Suppress("NAME_SHADOWING")
	DisposableEffect(firstChart, secondChart) {
		val firstChart = firstChart ?: return@DisposableEffect onDispose { }
		val secondChart = secondChart ?: return@DisposableEffect onDispose { }
		verticalSync.addAllCharts(firstChart, secondChart)

		firstChart.onZoom { secondChart.zoom(it.zoomAction) }
		firstChart.onPan { secondChart.pan(it.panAction) }
		firstChart.onViewReset { secondChart.viewReset() }
		firstChart.onHighlight { event ->
			secondChart.highlight(event.data)
			secondChart.setCursorFor(event.selectedData.firstOrNull())
		}
		onDispose {
			// We are not changing the charts.
			// If we did, we would want to unregister all the listeners here.
		}
	}
}
