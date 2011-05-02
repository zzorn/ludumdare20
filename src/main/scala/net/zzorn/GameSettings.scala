package net.zzorn

import controls.{SpeedSettings, PhysicsSettings}
import creatures.CreatureSettings
import level.Level
import org.scalaprops.Bean
import org.scalaprops.ui.editors.BeanEditorFactory

/**
 * Contains various settings
 */
class GameSettings extends Bean {

  val player = p('player, new CreatureSettings)
  val gemCreature = p('gems, new CreatureSettings)

  val level01 = p('level01, new Level)
  val level02 = p('level02, new Level)
  val level03 = p('level03, new Level)
  val level04 = p('level04, new Level)
  val level05 = p('level05, new Level)

  val cameraBehindDistance = p('cameraBehindDistance, 10f)
  val cameraAboveDistance = p('cameraAboveDistance, 5f)

}