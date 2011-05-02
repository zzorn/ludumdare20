package net.zzorn.appearance

import org.scalaprops.Bean
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.MathUtils
import util.Random
import net.zzorn.Settings

/**
 * 
 */
class SurfaceSettings extends Settings {

  val convexity = p('convexity, 6f).editor(makeSlider(0, 20))

  val offset = p('offset, 2f).editor(makeSlider(0, 20))
  val amplitude = p('amplitude, 6f).editor(makeSlider(0, 20))
  val scale = p('amplitude, 1.5f).editor(makeSlider(0, 5))
  val detailAmplitude = p('detailAmplitude, 1f).editor(makeSlider(0, 10))
  val detailScale = p('detailScale, 3f).editor(makeSlider(0, 10))

  val material = p('material, new MaterialSettings )
  val radial = p('radial, false)

  val convexityCutoff = p('convexityCutoff, 0.6f).editor(makeSlider(0, MathUtils.Tau * 2))

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