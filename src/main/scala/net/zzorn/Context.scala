package net.zzorn

import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState
import com.jme3.scene.Spatial
import java.util.ArrayList
import com.jme3.bounding.BoundingBox
import scala.collection.JavaConversions._

/**
 * Singleton for accessing services.
 */
object Context {

  val settings = new Settings

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

}