package net.zzorn

import org.scalaprops.ui.editors.SliderFactory._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import java.awt.Color
import org.scalaprops.Bean
import utils.Colors

/**
 * 
 */
trait Settings extends Bean {

  def makeSlider(start: Double = 0, end: Double = 1, hue: Double = 0.5, sat: Double = 0.2, lum: Double = 0.4): SliderFactory[Float] = {
    val col = Colors.HSLtoRGB(hue.toFloat, sat.toFloat, lum.toFloat, 1f)
    val painter = new ColoredSliderBackgroundPainter(Color.WHITE, new Color(col.r, col.g, col.b))
    new SliderFactory[Float](start.toFloat,
                             end.toFloat,
                             restrictNumberFieldMin = false,
                             restrictNumberFieldMax = false,
                             backgroundPainter = painter)
  }

  val baseEditor = makeSlider(hue=0.1, lum = 0.2)
  val spreadEditor = makeSlider(end = 0.5, hue=0.7, lum = 0.6)

}