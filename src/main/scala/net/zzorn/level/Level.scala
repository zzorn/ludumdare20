package net.zzorn.level

import com.jme3.scene.{Node, Spatial}
import net.zzorn.controls.WalkerControl
import simplex3d.math.float.functions._
import simplex3d.math.float._
import org.scalaprops.Bean
import util.Random
import com.jme3.bullet.util.CollisionShapeFactory
import com.jme3.bullet.control.RigidBodyControl
import net.zzorn.utils.VectorConversion._
import net.zzorn.utils.{Colors, XorShiftRng, RandomUtils}
import org.scalaprops.ui.editors.{BeanEditorFactory, SliderFactory}
import java.beans.PropertyEditor
import net.zzorn.controls.WalkerControl
import net.zzorn.appearance.{ColorSettings, ShapeUtils}
import net.zzorn.{Settings, Context}
import com.jme3.light.{AmbientLight, DirectionalLight}
import com.jme3.math.{Vector3f, ColorRGBA}
import net.zzorn.lights.{DirectionalLightSettings, AmbientLightSettings}

/**
 * 
 */
class Level extends Settings {

  val seed = p('seed, 1234)

//  val color = p('color, new ColorSettings)


  val numBoxes = p('numBoxes, 40)

  val platformType = p('platformType, new PlatformType())
  val goalPlatform = p('goalPlatform, new PlatformType())

  val areaX = p('areaX, 200f).editor(makeSlider(0, 1000))
  val areaY = p('areaY, 70f).editor(makeSlider(0, 1000))
  val areaZ = p('areaZ, 200f).editor(makeSlider(0, 1000))

  val goalX = p('goalX, 400f).editor(makeSlider(0, 1000))
  val goalY = p('goalY, 70f).editor(makeSlider(0, 1000))
  val goalZ = p('goalZ, 200f).editor(makeSlider(0, 1000))

  val spawnX = p('spawnX, 0f).editor(makeSlider(-1000, 1000))
  val spawnY = p('spawnY, 100f).editor(makeSlider(-1000, 1000))
  val spawnZ = p('spawnZ, 0f).editor(makeSlider(-1000, 1000))

  val sky = p('skyColor, new ColorSettings())

  val ambLight  = p('ambientLight, new AmbientLightSettings)
  val dirLight1 = p('directionalLight1, new DirectionalLightSettings)
  val dirLight2 = p('directionalLight2, new DirectionalLightSettings)
  val dirLight3 = p('directionalLight3, new DirectionalLightSettings)

  val killDepth = p('killDepth, -10000f)

  def spawnLocation: Vec3 = Vec3(spawnX(),spawnY(), spawnZ())

  def skyColor: ColorRGBA = sky().createColor(new Random(seed()))

  def configLights(ambientLight: AmbientLight,
                   directionalLight1: DirectionalLight,
                   directionalLight2: DirectionalLight,
                   directionalLight3: DirectionalLight) {
    val randomizer = new Random(seed() + 543235)
    ambLight().configure(ambientLight, randomizer)
    dirLight1().configure(directionalLight1, randomizer)
    dirLight2().configure(directionalLight2, randomizer)
    dirLight3().configure(directionalLight3, randomizer)
  }

  def generateSpatial(): Spatial = {
    val level = new Node()

    val randomizedSeed = new Random(seed()).nextLong()
    val rng = new Random()
    val area = Vec3(areaX(), areaY(), areaZ())
    for (val i <- 1 to numBoxes()) {
      rng.setSeed(i + randomizedSeed)

      val pos = RandomUtils.vec3(area, random = rng)

      val platform = platformType().createPlatform(pos, rng)

      level.attachChild(platform)

    }

    val pos = Vec3(goalX(), goalY(), goalZ())
    val goalPlat = goalPlatform().createPlatform(pos, rng)
    level.attachChild(goalPlat)


    /*
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
    */



    level
  }




}