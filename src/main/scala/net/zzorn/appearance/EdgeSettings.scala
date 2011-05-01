package net.zzorn.appearance

import org.scalaprops.Bean
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.MathUtils

/**
 * 
 */
class EdgeSettings extends Bean {

  val base = p('base, 40f)
  val amplitude = p('amplitude, 5f)
  val scale = p('amplitude, 1.3f)
  val detailAmplitude = p('detailAmplitude, 1f)
  val detailScale = p('detailScale, 3f)

  def edgeFunction(seed: Float): (Float) => Float = { (direction: Float) =>
    val sc = scale()
    val dsc = detailScale()
    base() +
      MathUtils.horizontallySeamlessNoise1(direction * sc, seed, sc) * amplitude() +
      MathUtils.horizontallySeamlessNoise1(direction * dsc, seed + 34234.234f, dsc) * detailAmplitude()
  }

}