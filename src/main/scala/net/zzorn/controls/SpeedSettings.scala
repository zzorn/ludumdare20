package net.zzorn.controls

import org.scalaprops.Bean
import net.zzorn.Settings

/**
 * 
 */

class SpeedSettings extends Settings {
  val strafeSpeed = p('strafeSpeed, 10f).editor(makeSlider(0, 100))
  val forwardSpeed = p('forwardSpeed, 30f).editor(makeSlider(0, 100))
  val backSpeed = p('backSpeed, 5f).editor(makeSlider(0, 100))
  val upSpeed = p('upSpeed, 0f).editor(makeSlider(0, 100))
  val downSpeed = p('downSpeed, 0f).editor(makeSlider(0, 100))


}