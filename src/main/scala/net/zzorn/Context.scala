package net.zzorn

import com.jme3.asset.AssetManager

/**
 * Singleton for accessing services.
 */
object Context {

  def assetManager: AssetManager = Ludum20.getAssetManager

}