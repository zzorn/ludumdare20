package net.zzorn

import com.jme3.scene.{Node, Spatial}
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.scalaprops.Bean
import org.scalaprops.ui.editors.SliderFactory
import util.Random
import com.jme3.bullet.util.CollisionShapeFactory
import com.jme3.bullet.control.RigidBodyControl
import com.jme3.math.ColorRGBA
import net.zzorn.utils.VectorConversion._
import utils.{Colors, XorShiftRng, RandomUtils, ShapeUtils}


/**
 * 
 */
class Level extends Bean {

  val baseEditor = new SliderFactory[Float](0, 1)
  val spreadEditor = new SliderFactory[Float](0, 0.5f)

  val seed = p('seed, 1234)

  val hueBase = p('hueBase, 0.4f).editor(baseEditor)
  val hueSpread = p('hueSpread, 0.1f).editor(spreadEditor)
  val satBase = p('satBase, 0.3f).editor(baseEditor)
  val satSpread = p('satSpread, 0.3f).editor(spreadEditor)
  val lumBase = p('lumBase, 0.4f).editor(baseEditor)
  val lumSpread = p('lumSpread, 0.25f).editor(spreadEditor)

  val numBoxes = p('numBoxes, 200)

  val areaX = p('areaX, 200f)
  val areaY = p('areaY, 70f)
  val areaZ = p('areaZ, 200f)

  val skyHue = p('skyHue, 0.25f).editor(baseEditor)
  val skySat = p('skySat, 0.5f).editor(baseEditor)
  val skyLum = p('skyLum, 0.5f).editor(baseEditor)


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
      val unClampedSize = RandomUtils.vec3(Vec3(40, 10, 50),
                                        Vec3(20, 20, 10),
                                        random = rng)

      val size = clamp(unClampedSize, 5f, 10000000000f)

      val color =  RandomUtils.hslColor(base = Vec4(hueBase(), satBase(), lumBase(), 1f),
                                        spread= Vec4(hueSpread(), satSpread(), lumSpread(), 0f),
                                        gaussian=true,
                                        random = rng)

      val box = ShapeUtils.createBox(pos, size, color)

      // Create physics collision shape
      val shape = CollisionShapeFactory.createBoxShape(box)
      box.addControl(new RigidBodyControl(shape, 0))
      Context.physicsState.getPhysicsSpace().add(box)

      level.attachChild(box)
    }

    level
  }




}