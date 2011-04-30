package net.zzorn.controls

import com.jme3.renderer.{ViewPort, RenderManager}
import com.jme3.scene.Spatial
import org.scalaprops.Bean
import com.jme3.scene.control.{Control, AbstractControl}
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.VectorConversion._
import com.jme3.math.{Quaternion, Vector3f}
import com.jme3.bounding.{BoundingVolume, BoundingSphere, BoundingBox}
import net.zzorn.Context

import scala.collection.JavaConversions._

/**
 * 
 */
class WalkerControl extends AbstractControl with Bean with Steerable {

  val strafeSpeed = p('strafeSpeed, 10f) onChange({updateMovementSpeed()})
  val forwardSpeed = p('forwardSpeed, 30f) onChange({updateMovementSpeed()})
  val backSpeed = p('backSpeed, 5f) onChange({updateMovementSpeed()})
  val upSpeed = p('upSpeed, 0f) onChange({updateMovementSpeed()})
  val downSpeed = p('downSpeed, 0f) onChange({updateMovementSpeed()})

  val gravityEffect = p('gravityEffect, 1f)
  val dragFactor = p('dragFactor, 0.5f)

  val heightAboveGround = p('heightAboveGround, 2f)

  val movementSpeedPlus: Vec3 = Vec3(0,0,0)
  val movementSpeedMinus: Vec3 = Vec3(0,0,0)

  private var support: Spatial = null

  updateMovementSpeed()

  private def updateMovementSpeed() {
    movementSpeedPlus.x = forwardSpeed()
    movementSpeedPlus.y = upSpeed()
    movementSpeedPlus.z = strafeSpeed()
    movementSpeedMinus.x = backSpeed()
    movementSpeedMinus.y = downSpeed()
    movementSpeedMinus.z = strafeSpeed()
  }

  def onGround = support != null

  def cloneForSpatial(spatial: Spatial): Control = {
    val control = new WalkerControl()
    spatial.addControl(control)
    return control
  }

  def controlRender(rm: RenderManager, vp: ViewPort) {}

  def controlUpdate(tpf: Float) {

    val oldPos = new Vector3f(spatial.getLocalTranslation)

    // Normalize heading
    if ( heading.x == 0 &&
         heading.y == 0 &&
         heading.z == 0) heading.x = 1f
    else heading := normalize(heading)

    // Clamp movement
    steeringMovement := clamp(steeringMovement, -1f, 1f)

    // Point the creature in the heading direction
    val horizontalHeading = new Vector3f(heading.x, 0, heading.z)
    val up = Vector3f.UNIT_Y
    val rotation: Quaternion = new Quaternion()
    rotation.lookAt(horizontalHeading, up)
    spatial.setLocalRotation(rotation)

    if (onGround) {
      // Calculate movement
      val mPlus = clamp(steeringMovement, 0f, 1f) * movementSpeedPlus
      val mMinus = clamp(steeringMovement, -1f, 0f) * movementSpeedMinus
      val m: Vector3f = (mPlus + mMinus) * tpf

      // Calculate position
      val pos = spatial.getLocalTranslation.add(spatial.getLocalRotation.mult(m))

      // Set vertical position to the spatial surface
      val yBase: Float = getSupportHeightAt(pos, support)
      pos.y = yBase + heightAboveGround()

      // Update velocity
      velocity := Vec3(pos.x - oldPos.x,
                       pos.y - oldPos.y,
                       pos.z - oldPos.z)

      // Update pos
      spatial.setLocalTranslation(pos)

      // Check for falling off the spatial
      val onSupport: Boolean = isOnSupport(pos, support)
      if (!onSupport) support = null
    }
    else {
      // Apply gravity
      velocity -= Vec3.UnitY * gravityEffect() * tpf

      // Apply air resistance (in turbulent flow the drag is approximately velocity squared * some constant,
      // in low speed laminar flow (no turbulence at all) the drag is the velocity * some constant)
      // See e.g. http://en.wikipedia.org/wiki/Drag_equation
      velocity -= velocity * velocity * dragFactor() * tpf
    }

    // Check for colliding with a spatial (other than any support we may be on)
    val ourPos = spatial.getWorldTranslation
    var currentPlatformHeight = if (support != null) getSupportHeightAt(ourPos, support) else scala.Float.NegativeInfinity
    Context.platforms foreach {platform =>
      if (platform != support) {
        if (platform.getWorldBound.contains(ourPos) && isOnSupport(ourPos, platform)) {
          val platformHeight = getSupportHeightAt(ourPos, platform)
          if (platformHeight > currentPlatformHeight) {
            // Move to higher platform
            support = platform
            currentPlatformHeight = platformHeight

            // Stop falling
            // TODO: Maybe small bounce?
            velocity.y = 0f 
          }
        }
      }
    }

  }

  private def getSupportHeightAt(pos: Vector3f, support: Spatial): Float = {
    support.getWorldBound match {
      case b: BoundingBox => b.getCenter.y + b.getYExtent
      case b: BoundingSphere => b.getCenter.y + b.getRadius * 0.8f
      case t =>
        println("Unknown bounding volume " + t)
        pos.y - heightAboveGround()
    }
  }

  private def isOnSupport(pos: Vector3f, support: Spatial): Boolean = {
    support.getWorldBound match {
      case b: BoundingBox =>
        pos.x <= b.getCenter.x + b.getXExtent &&
        pos.x >= b.getCenter.x - b.getXExtent &&
        pos.z <= b.getCenter.z + b.getZExtent &&
        pos.z >= b.getCenter.z - b.getZExtent
      case b: BoundingSphere =>
        val projectedCenter = new Vector3f(b.getCenter)
        projectedCenter.y = pos.y
        pos.distance(projectedCenter) <= b.getRadius * 0.8f
      case t =>
        println("Unknown bounding volume " + t)
        false
    }
  }



}