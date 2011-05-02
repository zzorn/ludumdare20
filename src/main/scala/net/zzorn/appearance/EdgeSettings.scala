package net.zzorn.appearance

import org.scalaprops.Bean
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.MathUtils
import net.zzorn.Settings

/**
 * 
 */
class EdgeSettings extends Settings {

  val base = p('base, 40f).editor(makeSlider(0, 100))
  val amplitude = p('amplitude, 20f).editor(makeSlider(0, 40))
  val scale = p('amplitude, 2.3f).editor(makeSlider(0, 5))
  val detailAmplitude = p('detailAmplitude, 4f).editor(makeSlider(0, 10))
  val detailScale = p('detailScale, 3f).editor(makeSlider(0, 20))

  def edgeFunction(seed: Float): (Float) => Float = { (direction: Float) =>
    val sc = scale()
    val dsc = detailScale()
    val detailSeed = seed + 34234.234f
    base() +
      MathUtils.horizontallySeamlessNoise1(direction * sc, seed, sc) * amplitude() +
      MathUtils.horizontallySeamlessNoise1(direction * dsc, detailSeed, dsc) * detailAmplitude()
  }

}