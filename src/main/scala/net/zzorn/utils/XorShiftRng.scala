package net.zzorn.utils

object XorShiftRng {
  private val defaultSeed1: Int = 123456789
  private val defaultSeed2: Int = 362436069
  private val defaultSeed3: Int = 521288629
  private val defaultSeed4: Int = 88675123
}

/**
 * 
 */
// TODO: Broken, fix.  Not in use atm.
class XorShiftRng {

  private var x = XorShiftRng.defaultSeed1
  private var y = XorShiftRng.defaultSeed2
  private var z = XorShiftRng.defaultSeed3
  private var w = XorShiftRng.defaultSeed4

  private var _haveNextGaussian = false
  private var _nextGaussian = 0.0

  def this(seed1: Int,
           seed2: Int = XorShiftRng.defaultSeed2,
           seed3: Int = XorShiftRng.defaultSeed3,
           seed4: Int = XorShiftRng.defaultSeed4) {
    this()
    seed(seed1, seed2, seed3, seed4)
  }

  def seed(seed1: Int = XorShiftRng.defaultSeed1,
           seed2: Int = XorShiftRng.defaultSeed2,
           seed3: Int = XorShiftRng.defaultSeed3,
           seed4: Int = XorShiftRng.defaultSeed4) {

    x = if (seed1 == 0) XorShiftRng.defaultSeed1 else seed1
    y = if (seed2 == 0) XorShiftRng.defaultSeed2 else seed2
    z = if (seed3 == 0) XorShiftRng.defaultSeed3 else seed3
    w = if (seed4 == 0) XorShiftRng.defaultSeed4 else seed4

    // Cycle in seed values
    nextInt
    nextInt
    nextInt
    nextInt
    nextInt
    nextInt
  }

  def nextInt: Int = {
    val t = x ^ (x << 11)
    x = y; y = z; z = w;
    w = w ^ (w >> 19) ^ (t ^ (t >> 8))
    w
  }

  def nextInt(max: Int): Int = {
    // TODO: Naive implementation, not good for large values.  Fix.
    math.abs(nextInt) % max
  }

  def nextFloat: Float = {
    // TODO: Naive implementation.  Fix.
    nextDouble.toFloat
  }

  def nextGaussianF: Float = nextGaussian.toFloat

  def nextGaussian: Double = {
    if (_haveNextGaussian) {
      _haveNextGaussian = false
      _nextGaussian
    }
    else {
      var t  = 0.0
      var v1 = 0.0
      var v2 = 0.0
      do {
        v1 = 2.0 * nextDouble - 1.0
        v2 = 2.0 * nextDouble - 1.0
        t = v1 * v1 + v2 * v2;
      } while (t >= 1 || t==0 )

      val multiplier = math.sqrt(-2.0 * math.log(t) / t)

      _nextGaussian = v2 * multiplier
      _haveNextGaussian = true

      v1 * multiplier
    }
  }

  def nextDouble: Double = {
    val a: Long = (nextInt.toLong & 0xffffffffL) >>> 5
    val b: Long = (nextInt.toLong & 0xffffffffL) >>> 6
    (a * 67108864.0 + b) / 9007199254740992.0
  }


}