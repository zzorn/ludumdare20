package net.zzorn.controls

import org.scalaprops.Bean
import net.zzorn.Settings

/**
 * 
 */

class PhysicsSettings extends Settings {
  val gravityEffect = p('gravityEffect, 0.2f).editor(makeSlider(0, 4))
  val dragFactor = p('dragFactor, 0.2f).editor(makeSlider(0, 1))
  val jumpSpeed = p('jumpSpeed, 0.1f).editor(makeSlider(0, 3))

}