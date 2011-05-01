package net.zzorn.level

import com.jme3.scene.{Node, Spatial}
import net.zzorn.controls.WalkerControl
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.scalaprops.Bean
import util.Random
import com.jme3.bullet.util.CollisionShapeFactory
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.math.ColorRGBA
import net.zzorn.utils.VectorConversion._
import net.zzorn.utils.{Colors, XorShiftRng, RandomUtils}
import org.scalaprops.ui.editors.{BeanEditorFactory, SliderFactory}
import java.beans.PropertyEditor
import net.zzorn.controls.WalkerControl
import net.zzorn.Context
import net.zzorn.appearance.{ColorSettings, ShapeUtils}


/**
 * 
 */
class Level extends Bean {

  val baseEditor = new SliderFactory[Float](0, 1)
  val spreadEditor = new SliderFactory[Float](0, 0.5f)

  val seed = p('seed, 1234)

//  val color = p('color, new ColorSettings)

  val hueBase = p('hueBase, 0.4f).editor(baseEditor)
  val hueSpread = p('hueVariation, 0.1f).editor(spreadEditor)
  val satBase = p('satBase, 0.3f).editor(baseEditor)
  val satSpread = p('satVariation, 0.3f).editor(spreadEditor)
  val lumBase = p('lumBase, 0.4f).editor(baseEditor)
  val lumSpread = p('lumVariation, 0.25f).editor(spreadEditor)

  val numBoxes = p('numBoxes, 40)

  val platformType = p[Bean]('platformType, new PlatformType()).editor(new BeanEditorFactory())

  val areaX = p('areaX, 200f)
  val areaY = p('areaY, 70f)
  val areaZ = p('areaZ, 200f)

  val skyHue = p('skyHue, 0.1f).editor(baseEditor)
  val skySat = p('skySat, 0.4f).editor(baseEditor)
  val skyLum = p('skyLum, 0.8f).editor(baseEditor)

  def spawnLocation: Vec3 = Vec3(0,200, 0)

  def skyColor: ColorRGBA = Colors.HSLtoRGB(skyHue(), skySat(), skyLum(), 1f)

  def generateSpatial(): Spatial = {
    val level = new Node()

    val randomizedSeed = new Random(seed()).nextLong()
    val rng = new Random()
    for (val i <- 1 to numBoxes()) {
      rng.setSeed(i + randomizedSeed)

      val area = Vec3(areaX(), areaY(), areaZ())
      val pos = RandomUtils.vec3(area,
                                 random = rng)

      // TODO: Fix bean editor factory typing in scalaprops to avoid unnecessary cast
      val platform = platformType().asInstanceOf[PlatformType].createPlatform(pos, rng)

      level.attachChild(platform)

    }

    // Create some test objs
    for (val i <- 1 to numBoxes()) {
      rng.setSeed(i + randomizedSeed + 3245)

      val area = Vec3(areaX(), areaY(), areaZ())
      val pos = RandomUtils.vec3(area, random = rng)
      pos.y += 300
      val unClampedSize = RandomUtils.vec3(Vec3(3, 3, 3),
                                        Vec3(5, 4, 5),
                                        random = rng)

      val size = clamp(unClampedSize, 2f, 10000000000f)

      val color =  RandomUtils.hslColor(base = Vec4(hueBase() + 0.5f, satBase(), lumBase(), 1f),
                                        spread= Vec4(hueSpread(), satSpread(), lumSpread(), 0f),
                                        gaussian=true,
                                        random = rng)

      val blob = ShapeUtils.createSphere(pos, size, color)

      val walkerControl = new WalkerControl(Context.settings.gemCreature())

      blob.addControl(walkerControl)

      level.attachChild(blob)

      // Create physics collision shape
      //val shape = CollisionShapeFactory.createBoxShape(box)
      //blob.addControl(new RigidBodyControl(3f))

    }



    level
  }




}