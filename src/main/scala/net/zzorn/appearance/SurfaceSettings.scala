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
  val radial = p('radial, false)

  val convexityCutoff = p('convexityCutoff, 0.6f)

  def surfaceFunction(seed: Float): (Float, Float) => Float = { (side: Float, distance: Float) =>
    val sc = scale()
    val dsc = detailScale()
    val a = amplitude()
    val detailSeed = seed+ 342.1234f
    val c = convexity() * (math.cos(distance * math.Pi * convexityCutoff()).toFloat + 1f) * 0.5f // Scale cos output to range 0..1
    if (radial()) {
      offset() + c +
        MathUtils.horizontallySeamlessNoise2(side * sc, distance * sc, seed, sc) * amplitude() +
        MathUtils.horizontallySeamlessNoise2(side * dsc, distance * dsc, detailSeed, dsc) * detailAmplitude()
    }
    else {
      offset() +  c +
        noise1(Vec3(seed,       MathUtils.polarToCartesian(distance, side * MathUtils.Tau) * sc )) * amplitude() +
        noise1(Vec3(detailSeed, MathUtils.polarToCartesian(distance, side * MathUtils.Tau) * dsc)) * detailAmplitude()
    }
  }

}