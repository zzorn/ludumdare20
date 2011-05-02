package net.zzorn.controls
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.VectorConversion._

/**
 * Something that can be steered
 */
trait Steerable {

  val heading: Vec3 = Vec3(0, 0, 1)
  val leftDir: Vec3 = Vec3(1, 0, 0)

  val steeringMovement: Vec3 = Vec3(0, 0, 0)

  val velocity: Vec3 = Vec3(0, 0, 0)

  var jump: Boolean = false

  def reset() {
    heading := Vec3(0, 0, 1)
    leftDir := Vec3(1, 0, 0)
    velocity := Vec3.Zero
    steeringMovement := Vec3.Zero
    jump = false
  }

}