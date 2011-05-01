package net.zzorn.appearance

import org.scalaprops.Bean
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.MathUtils
import util.Random

/**
 * 
 */
class SurfaceSettings extends Bean {

  val convexity = p('convexity, 6f)

  val offset = p('offset, 2f)
  val amplitude = p('amplitude, 6f)
  val scale = p('amplitude, 1.5f)
  val detailAmplitude = p('detailAmplitude, 1f)
  val detailScale = p('detailScale, 3f)

  val material = p('material, new MaterialSettings )


  def surfaceFunction(seed: Float): (Float, Float) => Float = { (side: Float, distance: Float) =>
    val sc = scale()
    val dsc = detailScale()
    val a = amplitude()
    offset() +
      MathUtils.horizontallySeamlessNoise2(side * sc, distance * sc, seed, sc) * amplitude() +
      MathUtils.horizontallySeamlessNoise2(side * dsc, distance * dsc, seed + 342.1234f, dsc) * detailAmplitude() +
      convexity() * (math.cos(distance * math.Pi).toFloat + 1f) * 0.5f // Scale cos output to range 0..1
  }

}