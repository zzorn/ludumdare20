package net.zzorn.controls

import org.scalaprops.Bean

/**
 * 
 */

class PhysicsSettings extends Bean {
  val gravityEffect = p('gravityEffect, 0.2f)
  val dragFactor = p('dragFactor, 0.05f)
  val jumpSpeed = p('jumpSpeed, 0.1f)

}