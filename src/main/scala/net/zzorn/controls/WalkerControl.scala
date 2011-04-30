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
import net.zzorn.creatures.CreatureSettings

/**
 * 
 */
class WalkerControl(creature: CreatureSettings) extends AbstractControl with Steerable {

  private val movementSpeedPlus: Vec3 = Vec3(0,0,0)
  private val movementSpeedMinus: Vec3 = Vec3(0,0,0)

  var loggingOn = false

  private var support: Spatial = null

  def onGround = support != null

  def cloneForSpatial(spatial: Spatial): Control = {
    val control = new WalkerControl(creature)
    spatial.addControl(control)
    return control
  }

  def controlRender(rm: RenderManager, vp: ViewPort) {}

  override def reset() {
    super.reset()
    support = null
  }

  def controlUpdate(tpf: Float) {
    if (loggingOn) println("controlUpdate called " + tpf)

    val oldPos = new Vector3f(spatial.getLocalTranslation)
    var moved = false

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
      if (loggingOn) println("  on ground " + support)

      // Calculate movement
      val m: Vec3 = calculateMovementSpeed() * tpf

      // Calculate position
      val pos = spatial.getLocalTranslation.add(spatial.getLocalRotation.mult(m))

      // Set vertical position to the spatial surface
      val yBase: Float = getSupportHeightAt(pos, support)
      pos.y = yBase + groundClearance

      // Update velocity (for when we walk of an edge, or jump)
      velocity := Vec3(pos.x - oldPos.x,
                       pos.y - oldPos.y,
                       pos.z - oldPos.z)

      moved = velocity.lengthSquared > 0

      // Update pos
      spatial.setLocalTranslation(pos)

      // Check for falling off the spatial
      val onSupport: Boolean = isOnSupport(pos, support)
      if (!onSupport) support = null

      // Check for jump
      if (onSupport && jump) {
        if (loggingOn) println("  jumped")
        support = null
        velocity.y = creature.physics().jumpSpeed()
        jump = false
      }

      if (loggingOn) println("  still supported " + onSupport)
    }
    else {
      if (loggingOn) println("  flying " + support)

      // Apply gravity
      velocity -= Vec3.UnitY * creature.physics().gravityEffect() * tpf

      // Apply air resistance (in turbulent flow the drag is approximately velocity squared * some constant,
      // in low speed laminar flow (no turbulence at all) the drag is the velocity * some constant)
      // See e.g. http://en.wikipedia.org/wiki/Drag_equation
      velocity -= velocity * velocity * creature.physics().dragFactor() * tpf

      // Update position
      val pos = spatial.getLocalTranslation
      val x = pos.x + velocity.x
      val y = pos.y + velocity.y
      val z = pos.z + velocity.z
      spatial.setLocalTranslation(x, y, z)

      moved = true
    }

    if (loggingOn) println("  local pos " + spatial.getLocalTranslation)
    if (loggingOn) println("  world pos " + spatial.getWorldTranslation)
    if (loggingOn) println("  velocity " + velocity)

    // If we moved, check for colliding with a spatial (other than any support we may be on)
    if (moved) handlePlatformCollisions()
  }

  private def calculateMovementSpeed(): Vec3 = {
    movementSpeedPlus.x = creature.speed().forwardSpeed()
    movementSpeedPlus.y = creature.speed().upSpeed()
    movementSpeedPlus.z = creature.speed().strafeSpeed()
    movementSpeedMinus.x = creature.speed().backSpeed()
    movementSpeedMinus.y = creature.speed().downSpeed()
    movementSpeedMinus.z = creature.speed().strafeSpeed()

    val mPlus = clamp(steeringMovement, 0f, 1f) * movementSpeedPlus
    val mMinus = clamp(steeringMovement, -1f, 0f) * movementSpeedMinus
    mPlus + mMinus
  }

  private def groundClearance: Float = {
    spatial.getWorldBound match {
      case b: BoundingBox => b.getYExtent
      case b: BoundingSphere => b.getRadius
      case t =>
        println("Unknown bounding volume " + t)
        0f
    }
  }

  private def getSupportHeightAt(pos: Vector3f, support: Spatial): Float = {
    support.getWorldBound match {
      case b: BoundingBox => b.getCenter.y + b.getYExtent
      case b: BoundingSphere => b.getCenter.y + b.getRadius * 0.8f
      case t =>
        println("Unknown bounding volume " + t)
        pos.y - groundClearance
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

  private def handlePlatformCollisions() {
    val ourPos = new Vector3f(spatial.getWorldTranslation)
    ourPos.y -= groundClearance // Collision test against the point where we touch ground

    // Check if we are at all near platforms (this eliminates things that have fallen off the map)
    if (Context.platformBounds.contains(ourPos)) {
      var currentPlatformHeight = if (support != null) getSupportHeightAt(ourPos, support)
                                  else scala.Float.NegativeInfinity
      Context.platforms foreach { platform =>
        if (platform != support) {
          if (platform.getWorldBound.contains(ourPos) && isOnSupport(ourPos, platform)) {
            val platformHeight = getSupportHeightAt(ourPos, platform)
            if (platformHeight > currentPlatformHeight) {

              if (loggingOn) println("  collided with " + platform)

              // Move to higher platform
              support = platform
              currentPlatformHeight = platformHeight

              // Stop falling
              // TODO: Maybe small bounce?
              velocity.x = 0f
              velocity.y = 0f
              velocity.z = 0f
            }
          }
        }
      }
    }
    }




}