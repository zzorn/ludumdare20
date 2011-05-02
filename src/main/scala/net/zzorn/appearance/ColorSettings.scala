package net.zzorn.appearance

import org.scalaprops.Bean
import util.Random
import net.zzorn.utils.RandomUtils
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.scalaprops.ui.editors.{ColoredSliderBackgroundPainter, SliderFactory}
import java.awt.Color
import net.zzorn.Settings

/**
 * 
 */
class ColorSettings() extends Settings {

  // TODO: Separate UI name for beans, beanName is used when loading
  //beanName = Symbol(_name)

  val hue = p('hue, 0f).editor(baseEditor)
  val sat = p('sat, 0.5f).editor(baseEditor)
  val lum = p('lum, 0.5f).editor(baseEditor)

  val hueVariation = p('hueVariation, 0.1f).editor(spreadEditor)
  val satVariation = p('satVariation, 0.1f).editor(spreadEditor)
  val lumVariation = p('lumVariation, 0.1f).editor(spreadEditor)


  def createColor(random: Random = new Random, gaussian: Boolean = true): Vec4 = {
    RandomUtils.hslColor(base = Vec4(hue(), sat(), lum(), 1f),
                         spread = Vec4(hueVariation(), satVariation(), lumVariation(), 0f),
                         gaussian = gaussian,
                         random = random)
  }
}