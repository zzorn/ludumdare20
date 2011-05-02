package net.zzorn.level

import com.jme3.scene.Spatial
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.VectorConversion._
import org.scalaprops.Bean
import org.scalaprops.ui.editors.SliderFactory
import simplex3d.math.floatx.functions._
import net.zzorn.utils.{RandomUtils}
import util.Random
import net.zzorn.appearance.{ColorSettings, EdgeSettings, SurfaceSettings, ShapeUtils}
import net.zzorn.{Settings, Context}
import net.zzorn.controls.PlatformControl

/**
 * Represents some type of platform
 */
class PlatformType extends Settings {

  val topSurface = p('topSurface, new SurfaceSettings)
  val bottomSurface = p('bottomSurface, new SurfaceSettings)
  val edge = p('edge, new EdgeSettings)

  val wobbleAmplitude = p('wobbleAmplitude, 4f).editor(makeSlider(0, 20))
  val wobbleFrequency = p('wobbleFrequency, 5f).editor(makeSlider(0, 100))
  val goalPlatform = p('goalPlatform, false)

  def wobbleHeight(time: Float, seed: Float): Float = {
    noise1(Vec2(time * 0.001f * wobbleFrequency(), seed)) * wobbleAmplitude()
  }

  def createPlatform(pos: Vec3, rng: Random): Spatial = {
    val platform = ShapeUtils.createPlatform(pos, edge(), topSurface(), bottomSurface(), rng)

    // Add to platforms when generated
    Context.platforms.add(platform)

    platform.addControl(new PlatformControl(this, rng.nextFloat * 1123.13f, goalPlatform()))
    platform
  }



}