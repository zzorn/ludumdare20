package net.zzorn.controls

import com.jme3.scene.control.{AbstractControl, Control}
import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.Spatial
import net.zzorn.level.PlatformType

/**
 * Control platforms have.
 * Contains some metadata
 */
class PlatformControl(val platformType: PlatformType, seed: Float, val goal: Boolean) extends AbstractControl {

  def cloneForSpatial(spatial: Spatial): Control  = {
    val control = new PlatformControl(platformType, seed, goal)
    spatial.addControl(control)
    control
  }

  def controlRender(rm: RenderManager, vp: ViewPort) {}

  def controlUpdate(tpf: Float) {
    /* TODO: Debug
    cumulativeTime += tpf
    val wobble = platformType.wobbleHeight(cumulativeTime, seed)
    val pos = spatial.getLocalTranslation
    spatial.setLocalTranslation(pos.x, pos.y + wobble, pos.z) // Might drift off..
    */
  }

  var cumulativeTime: Float = 0f


}