package net.zzorn.creatures

import org.scalaprops.Bean
import org.scalaprops.ui.editors.BeanEditorFactory
import net.zzorn.controls.{SpeedSettings, PhysicsSettings}

/**
 * 
 */

class CreatureSettings extends Bean {

  // TODO: Fix beaneditor issue
  val physics = p('physics, new PhysicsSettings) // .editor(new BeanEditorFactory())
  val speed   = p('speed, new SpeedSettings) // .editor(new BeanEditorFactory())
  val airSpeed= p('airSpeed, new SpeedSettings) // .editor(new BeanEditorFactory())
  val radius  = p('radius, 1f)
  val height  = p('height, 3f)

}