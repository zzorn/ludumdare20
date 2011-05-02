package net.zzorn.lights

import com.jme3.math.{Vector3f, ColorRGBA}
import net.zzorn.Settings
import com.jme3.light.{AmbientLight, DirectionalLight}
import net.zzorn.appearance.ColorSettings
import net.zzorn.utils.VectorConversion._

import util.Random


/**
 * 
 */

class AmbientLightSettings extends Settings {

  val color = p('color, new ColorSettings)
  val intensity = p('intensity, 1f).editor(makeSlider(0, 4))

  def configure(light: AmbientLight, random: Random) {
    light.setColor(color().createColor(random) * intensity())
    light.setColor(new ColorRGBA(0.3f, 0.2f, 0.1f, 1f).multLocal(3f))
  }
}