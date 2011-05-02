package net.zzorn

import appearance.{SurfaceSettings, MaterialSettings, EdgeSettings, ColorSettings}
import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState
import com.jme3.scene.Spatial
import controls.{SpeedSettings, PhysicsSettings}
import creatures.CreatureSettings
import java.util.ArrayList
import com.jme3.bounding.BoundingBox
import level.{PlatformType, Level}
import lights.DirectionalLightSettings
import lights.AmbientLightSettings
import scala.collection.JavaConversions._
import org.scalaprops.parser.{BeanParser, JsonBeanParser}
import org.scalaprops.Bean

/**
 * Singleton for accessing services.
 */
object Context {

  val beanParser = createBeanParser

  var settings = new GameSettings

  // TODO: Make into a physics ApplicationState
  val platforms: ArrayList[Spatial] = new ArrayList()
  var platformBounds: BoundingBox = new BoundingBox()

  def updatePlatformBounds() {
    platformBounds = new BoundingBox()

    platforms foreach {platform =>
      platformBounds.mergeLocal(platform.getWorldBound)
    }
  }

  def assetManager: AssetManager = Ludum20.getAssetManager

  //def physicsState: BulletAppState = Ludum20.bulletAppState


  private def createBeanParser: BeanParser = {

    val beanParser = new JsonBeanParser()

    def registerBeanType[T <: Bean](kind: Class[T]) {
      val name = Symbol(kind.getSimpleName)
      beanParser.beanFactory.registerBeanType(name, {() => kind.newInstance})
    }

    registerBeanType(classOf[EdgeSettings])
    registerBeanType(classOf[MaterialSettings])
    registerBeanType(classOf[SurfaceSettings])
    registerBeanType(classOf[PhysicsSettings])
    registerBeanType(classOf[SpeedSettings])
    registerBeanType(classOf[ColorSettings])
    registerBeanType(classOf[CreatureSettings])
    registerBeanType(classOf[PlatformType])
    registerBeanType(classOf[Level])
    registerBeanType(classOf[GameSettings])
    registerBeanType(classOf[DirectionalLightSettings])
    registerBeanType(classOf[AmbientLightSettings])

    beanParser
  }

}