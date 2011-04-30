package net.zzorn.utils

import simplex3d.math.float.functions._
import simplex3d.math.float._

/**
 * 
 */
object Colors {

  val red = Vec4(1,0,0,1)
  val blue = Vec4(0,0,1,1)


  /**
   * Convert a Hue Saturation Lightness color to Red Green Blue color space.
   * Algorithm based on the one in wikipedia ( http://en.wikipedia.org/wiki/HSL_color_space )
   * Copied from the open source rasterfun project.
   */
  def HSLtoRGB(hue: Float, saturation: Float, lightness: Float, alpha: Float): Vec4 = {

    if (lightness == 0) {
      // Black
      Vec4(0,0,0,alpha)
    }
    else if (lightness == 1) {
      // White
      Vec4(1,1,1,alpha)
    }
    else if (saturation == 0) {
      // Grayscale
      Vec4(lightness,lightness,lightness,alpha)
    }
    else {
      // Arbitrary color

      def hueToColor(p: Float, q: Float, t: Float): Float = {
        var th = t
        if (th < 0) th += 1
        if (th > 1) th -= 1
        if (th < 1f / 6f) return p + (q - p) * 6f * th
        if (th < 1f / 2f) return q
        if (th < 2f / 3f) return p + (q - p) * (2f / 3f - th) * 6f
        return p
      }

      val q = if (lightness < 0.5f) (lightness * (1f + saturation)) else (lightness + saturation - lightness * saturation)
      val p = 2 * lightness - q;
      var r = hueToColor(p, q, hue + 1f / 3f)
      var g = hueToColor(p, q, hue)
      var b = hueToColor(p, q, hue - 1f / 3f)

      // Clamp
      if (r < 0f) r = 0f
      else if (r > 1f) r = 1f

      if (g < 0f) g = 0f
      else if (g > 1f) g = 1f

      if (b < 0f) b = 0f
      else if (b > 1f) b = 1f

      Vec4(r, g, b, alpha)
    }
  }


}