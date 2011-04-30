package net.zzorn

import controls.{SpeedSettings, PhysicsSettings}
import creatures.CreatureSettings
import org.scalaprops.Bean
import org.scalaprops.ui.editors.BeanEditorFactory

/**
 * Contains various settings
 */
class Settings extends Bean {

  val player = p('player, new CreatureSettings)
  val gemCreature = p('gems, new CreatureSettings)

  val cameraBehindDistance = p('cameraBehindDistance, 10f)
  val cameraAboveDistance = p('cameraAboveDistance, 5f)

}