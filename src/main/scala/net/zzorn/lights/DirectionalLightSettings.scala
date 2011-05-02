package net.zzorn.lights

import net.zzorn.Settings
import com.jme3.light.DirectionalLight
import com.jme3.math.{Vector3f, ColorRGBA}
import net.zzorn.appearance.ColorSettings
import net.zzorn.utils.VectorConversion._
import util.Random

/**
 * 
 */

class DirectionalLightSettings extends Settings {

  val color = p('color, new ColorSettings)
  val intensity = p('intensity, 1f).editor(makeSlider(0, 4))
  val x = p('x, 0.3f).editor(makeSlider(-1, 1))
  val y = p('y, 0.5f).editor(makeSlider(-1, 1))
  val z = p('z, 0.1f).editor(makeSlider(-1, 1))

  def configure(light: DirectionalLight, random: Random) {
    light.setColor(color().createColor(random) * intensity())
    light.setDirection(new Vector3f(x(), y(), z()).normalizeLocal())
  }
}