package net.zzorn.utils
import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * 
 */
object MathUtils {

  // See http://www.tauday.org
  val Tau: Float = math.Pi.toFloat * 2f

  def polarToCartesian(r: Float, a: Float): Vec2  = Vec2(r * math.cos(a).toFloat,
                                                         r * math.sin(a).toFloat)

  def polarToCartesian3(r: Float, a: Float, height: Float): Vec3 = Vec3(r * math.cos(a).toFloat,
                                                                        height,
                                                                        r * math.sin(a).toFloat)

  def cartesianToPolar(x: Float, y: Float): Vec2 = Vec2(math.sqrt(x*x + y*y).toFloat,
                                                        math.atan2(y, x).toFloat)

  def horizontallySeamlessNoise2(x: Float, y: Float, seed: Float, width: Float): Float = {
    // NOTE: Not optimal tiling noise, but will do for now. Creates more averaged values in center.
    val projectedX = x % width
    val a = noise1(Vec3(projectedX, y, seed)) // Goes from x 0 to width
    val b = noise1(Vec3(projectedX - width, y, seed)) // Goes from x - width to 0

    mix(a, b, projectedX) // side 0 and side 1 have same noise (coordinate 0)
  }

  def horizontallySeamlessNoise1(x: Float, seed: Float, width: Float): Float = {
    // NOTE: Not optimal tiling noise, but will do for now. Creates more averaged values in center.
    val projectedX = x % width
    val a = noise1(Vec2(projectedX, seed)) // Goes from x 0 to width
    val b = noise1(Vec2(projectedX - width, seed)) // Goes from x - width to 0

    mix(a, b, projectedX) // side 0 and side 1 have same noise (coordinate 0)
  }

}