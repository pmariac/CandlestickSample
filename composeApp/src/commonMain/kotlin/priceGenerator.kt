package io.data2viz.sample

import io.data2viz.charts.chart.mark.domainSpecific.PriceMovement
import io.data2viz.random.RandomDistribution
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.PI
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin
import kotlin.time.Duration.Companion.hours

val randomGenerator =
	RandomDistribution(Clock.System.now().nanosecondsOfSecond).normal(.0, PI / 20.0)
val interval = 1.hours
val startPrice = 39477.4
var closePrice = startPrice


/**
 * Generate a random dataset
 */
fun generateDataset(startTime: Instant, samples: Int): List<PriceMovement> {
	return (1..samples).map {
		val p1 = closePrice + (sin(randomGenerator() * it) * 100)
		val p2 = closePrice + (sin(randomGenerator() * it) * 100)
		val p3 = closePrice + (sin(randomGenerator() * it) * 100)
		val prices = mutableListOf(p1, p2, p3)

		var high = prices.max()
		prices.remove(high)
		high = max(closePrice, high)

		var low = prices.min()
		prices.remove(low)
		low = min(closePrice, low)

		val open = closePrice
		val close = prices.first()

		val volume = 4000 + (sin(randomGenerator() * it) * 3500)
		closePrice = close
		PriceMovement(
			timestamp = startTime.plus(interval * it),
			interval = interval,
			open = open,
			close = close,
			high = high,
			low = low,
			volume = volume,
			turnover = 40000000.0,
		)
	}
}
