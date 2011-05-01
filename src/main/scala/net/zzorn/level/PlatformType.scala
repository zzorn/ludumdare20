package net.zzorn.level

import com.jme3.scene.Spatial
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.VectorConversion._
import org.scalaprops.Bean
import org.scalaprops.ui.editors.SliderFactory
import simplex3d.math.floatx.functions._
import net.zzorn.utils.{RandomUtils}
import net.zzorn.Context
import util.Random
import net.zzorn.appearance.ShapeUtils

/**
 * Represents some type of platform
 */
class PlatformType extends Bean {

  val baseEditor = new SliderFactory[Float](0, 1)
  val spreadEditor = new SliderFactory[Float](0, 0.5f)

  val hueBase = p('hueBase, 0.4f).editor(baseEditor)
  val hueSpread = p('hueVariation, 0.1f).editor(spreadEditor)
  val satBase = p('satBase, 0.3f).editor(baseEditor)
  val satSpread = p('satVariation, 0.3f).editor(spreadEditor)
  val lumBase = p('lumBase, 0.4f).editor(baseEditor)
  val lumSpread = p('lumVariation, 0.25f).editor(spreadEditor)

  val horizontalSize = p('horizontalSize, 20f)
  val horizontalSizeVariation = p('horizontalSizeVariation, 20f)

  val thickness = p('thickness, 10f)
  val thicknessVariation = p('thicknessVariation, 5f)

  val minSize = p('minSize, 10f)

  def createPlatform(pos: Vec3, rng: Random): Spatial = {
    val h = horizontalSize()
    val hv = horizontalSizeVariation()
    val t = thickness()
    val tv = thicknessVariation()

    val unClampedSize = RandomUtils.vec3(Vec3(hv, tv, hv), Vec3(h, t, h), random = rng)
    val size = clamp(unClampedSize, minSize(), scala.Float.PositiveInfinity)

    val color =  RandomUtils.hslColor(base = Vec4(hueBase(), satBase(), lumBase(), 1f),
                                      spread= Vec4(hueSpread(), satSpread(), lumSpread(), 0f),
                                      gaussian=true,
                                      random = rng)

    val box = ShapeUtils.createBox(pos, size, color)


    // Add to platforms when generated
    Context.platforms.add(box)

    box
  }



}