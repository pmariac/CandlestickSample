package io.data2viz.sample

import io.data2viz.charts.chart.mark.domainSpecific.PriceMovement
import io.data2viz.random.RandomDistribution
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.math.PI
import kotlin.math.sin
import kotlin.time.Duration.Companion.hours

val from = Instant.parse("2023-03-12T16:00:00.000Z")
val randomGenerator =
	RandomDistribution(Clock.System.now().nanosecondsOfSecond).normal(.0, PI / 20.0)
val interval = 1.hours
val startPrice = 39477.4
var closePrice = startPrice


/**
 * Generate a random dataset
 */
fun generateDataset(samples: Int): List<PriceMovement> {
	return (1..samples).map {
		val p1 = closePrice + (sin(randomGenerator() * it) * 100)
		val p2 = closePrice + (sin(randomGenerator() * it) * 100)
		val p3 = closePrice + (sin(randomGenerator() * it) * 100)
		val prices = listOf(p1, p2, p3)
		val high = prices.max()
		val low = prices.min()
		val open = closePrice
		val close = prices.first { it != high && it != low }
		val volume = 4000 + (sin(randomGenerator() * it) * 3500)
		closePrice = close
		PriceMovement(
			timestamp = from.plus(interval * it),
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
