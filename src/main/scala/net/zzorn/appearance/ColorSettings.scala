package net.zzorn.appearance

import org.scalaprops.Bean
import util.Random
import org.scalaprops.ui.editors.SliderFactory
import net.zzorn.utils.RandomUtils
import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * 
 */
class ColorSettings(_hue: Float = 0f,
                    _sat: Float = 0.5f,
                    _lum: Float = 0.5f,
                    _name: String = "Color") extends Bean {

  beanName = Symbol(_name)

  val baseEditor = new SliderFactory[Float](0, 1)
  val spreadEditor = new SliderFactory[Float](0, 0.5f)

  val hue = p('hueVariation, _hue).editor(baseEditor)
  val sat = p('hueVariation, _sat).editor(baseEditor)
  val lum = p('hueVariation, _lum).editor(baseEditor)

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