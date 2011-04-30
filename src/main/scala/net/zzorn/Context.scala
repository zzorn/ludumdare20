package net.zzorn

import com.jme3.asset.AssetManager
import com.jme3.bullet.BulletAppState

/**
 * Singleton for accessing services.
 */
object Context {

  def assetManager: AssetManager = Ludum20.getAssetManager

  //def physicsState: BulletAppState = Ludum20.bulletAppState

}