package io.data2viz.sample

import io.data2viz.charts.chart.Chart
import io.data2viz.charts.chart.chart
import io.data2viz.charts.chart.constant
import io.data2viz.charts.chart.discrete
import io.data2viz.charts.chart.mark.TooltipPosition
import io.data2viz.charts.chart.mark.candleStick
import io.data2viz.charts.chart.mark.domainSpecific.PriceMovement
import io.data2viz.charts.chart.mark.line
import io.data2viz.charts.chart.quantitative
import io.data2viz.charts.chart.temporal
import io.data2viz.charts.core.CursorDisplay
import io.data2viz.charts.core.CursorMode
import io.data2viz.charts.core.CursorType
import io.data2viz.charts.core.Font
import io.data2viz.charts.core.HighlightMode
import io.data2viz.charts.core.PanMode
import io.data2viz.charts.core.SelectedDatum
import io.data2viz.charts.core.SelectionMode
import io.data2viz.charts.core.TriggerMode
import io.data2viz.charts.core.ZoomMode
import io.data2viz.charts.core.formatToDateTime
import io.data2viz.charts.layout.DrawingZone
import io.data2viz.charts.viz.VizContainer
import io.data2viz.color.Colors
import io.data2viz.color.col
import io.data2viz.format.Locale
import io.data2viz.format.Type
import io.data2viz.format.formatter
import io.data2viz.timeFormat.defaultLocale
import io.data2viz.viz.RichTextBuilder


// Locales and formatters
private val locale = Locale()
private val amountFormatter = locale.formatter(type = Type.FIXED_POINT, precision = 1)
private val changeFormatter = locale.formatter(type = Type.PERCENT, precision = 2)
private val volumeFormatter = locale.formatter(type = Type.DECIMAL_WITH_SI, precision = 1)

/**
 * The candlestick chart.
 */
public fun VizContainer.candleStick(dataset: List<PriceMovement>): Chart<PriceMovement> {
    return chart(dataset) {

        config {
            events {

				// This allows the selection to be triggered by vertical position
                triggerMode = TriggerMode.Column

				// Disable selection
                selectionMode = SelectionMode.None

				// enable zoom and pan on X axis only
                zoomMode = ZoomMode.X
                panMode = PanMode.X
            }
            cursor {
                show = true

				// Cursor "sticks" on X values, but is free on Y position
                mode = CursorMode.StickOnX

				// Display the X and Y values of the current cursor position alongside the axes
                display = CursorDisplay.AxisXY
            }
        }

		/**
		 * Create a time dimension based on the timestamp, this dimension will be
		 * shared between the 2 marks (candlestick and line)
		 */
        val temporalDimension = temporal( { domain.timestamp } )

		/**
		 * Create a quantitative dimension that will also be shared between the marks.
		 */
		val meanPriceDimension = quantitative( { (domain.open + domain.close) / 2.0 } )

		// See the [richTooltip] lambda for more info
        tooltip {
			tooltipBuilder = richTooltip
        }

		/**
		 * The candlestick mark.
		 *
		 * This needs:
		 * 1. a continuous X dimension (here the temporal dimension)
		 * 2. a continuous Y dimension (here the price Dimension)
		 * 3. a lambda that returns a [PriceMovement] object
		 *
		 * Note: if you are using your own specific domain object, your dataset should be
		 * a list<mySpecificDomainObject>. Then you have to create something like :
		 *
		 * fun mySpecificDomainObject.toPriceDimension() : PriceDimension = ...
		 *
		 * Then, just use as the 3rd parameter the lambda { domain.toPriceDimension() }
		 */
		candleStick(temporalDimension, meanPriceDimension, { domain }) {
            strokeWidth = constant(2.0)

			/**
			 * As this mark is the main one, we set the axes properties here.
			 *
			 * - enableGridLines true : shows vertical and horizontal lines based on ticks positions
			 * - enableTicks false : hide the axes ticks
			 * - enableAxisLine false : hide the axes lines
			 */
            x {
                enableGridLines = true
                enableTicks = false
                enableAxisLine = false
            }
            y {
                enableGridLines = true
                enableTicks = false
                enableAxisLine = false
            }
        }

        // The "mean" line that is drawn on top of the candlestick and share the same dimensions
        line(temporalDimension, meanPriceDimension) {
            strokeWidth = constant(2.0)
            strokeColor = constant(Colors.Web.black)
            highlightMode = HighlightMode.Disabled
        }
    }
}

/**
 * The volume chart.
 *
 * NOTE: the [VariableColumn] class will be integrated in an upcoming version of Charts-kt
 * (we are currently testing it in release candidate), so you don't have to worry about
 * it as this will just be an import.
 */
public fun VizContainer.volumeHistogram(dataset: List<PriceMovement>): Chart<PriceMovement> {
    return chart(dataset) {

        config {

			/**
			 * Disable all events, so this charts can only be "controlled" through the first one
			 */
			events {
                triggerMode = TriggerMode.Column
                selectionMode = SelectionMode.None
                zoomMode = ZoomMode.None
                panMode = PanMode.None
            }
            cursor {
                show = true
                mode = CursorMode.StickOnX
                type = CursorType.Vertical
            }
            tooltip {
                show = false
            }
        }

        val temporalDimension = temporal( { domain.timestamp } )
        val volumeDimension = quantitative( { domain.volume } ) {
            formatter = { this?.let { volumeFormatter(it) } ?: "" }
        }

        variableColumn(temporalDimension, volumeDimension) {
            thickness = discrete( { domain.interval * .8 } )
            strokeColor = discrete( { if (domain.close < domain.open) "#CA3F66".col else "#25A750".col } )
            strokeColorHighlight = discrete({ strokeColor(this)?.brighten(1.0) } )
            fill = discrete( { if (domain.close < domain.open) "#CA3F66".col else "#25A750".col } )
            fillHighlight = discrete({ fill(this)?.brighten(1.0) } )

            x {
                enableGridLines = true
                enableTicks = false
                enableAxisLine = false
            }
            y {
                enableGridLines = true
                enableTicks = false
                enableAxisLine = false
                start = .0
            }
        }
    }
}

val richTooltip: (SelectedDatum<PriceMovement>?, TooltipPosition?, String?, String?, Double, Font, DrawingZone) -> RichTextBuilder =
	{ selectedData, ttp, defaultTitle, defaultText, displayRatio, defaultFont, drawingZone ->
		RichTextBuilder {
			val domain = selectedData?.datum?.domain ?: return@RichTextBuilder
			val change = (domain.close / domain.open) - 1.0
			text("Time    ", bold = true); text(
			domain.timestamp.formatToDateTime(
				defaultLocale
			)
		)
			newLine()
			text("Open    ", bold = true); text(amountFormatter(domain.open))
			newLine()
			text("High    ", bold = true); text(amountFormatter(domain.high))
			newLine()
			text("Low     ", bold = true); text(amountFormatter(domain.low))
			newLine()
			text("Close   ", bold = true); text(amountFormatter(domain.close))
			newLine()
			text("Chg.    ", bold = true); text(
			changeFormatter(change),
			textColor = if (change < 0) "#CA3F66".col else "#25A750".col
		)
		}
	}
