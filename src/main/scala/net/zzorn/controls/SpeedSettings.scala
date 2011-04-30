package net.zzorn.controls

import org.scalaprops.Bean

/**
 * 
 */

class SpeedSettings extends Bean {
  val strafeSpeed = p('strafeSpeed, 10f)
  val forwardSpeed = p('forwardSpeed, 30f)
  val backSpeed = p('backSpeed, 5f)
  val upSpeed = p('upSpeed, 0f)
  val downSpeed = p('downSpeed, 0f)

}