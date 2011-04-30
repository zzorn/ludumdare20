package net.zzorn

import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState
import com.jme3.scene.Spatial
import java.util.ArrayList

/**
 * Singleton for accessing services.
 */
object Context {

  val platforms: ArrayList[Spatial] = new ArrayList()

  def assetManager: AssetManager = Ludum20.getAssetManager

  //def physicsState: BulletAppState = Ludum20.bulletAppState

}