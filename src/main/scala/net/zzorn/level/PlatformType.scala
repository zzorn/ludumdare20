package net.zzorn.level

import com.jme3.scene.Spatial
import simplex3d.math.float.functions._
import simplex3d.math.float._
import net.zzorn.utils.VectorConversion._
import org.scalaprops.Bean
import org.scalaprops.ui.editors.SliderFactory
import simplex3d.math.floatx.functions._
import net.zzorn.utils.{RandomUtils}
import net.zzorn.Context
import util.Random
import net.zzorn.appearance.{ColorSettings, EdgeSettings, SurfaceSettings, ShapeUtils}

/**
 * Represents some type of platform
 */
class PlatformType extends Bean {

  val topSurface = p('topSurface, new SurfaceSettings)
  val bottomSurface = p('bottomSurface, new SurfaceSettings)
  val edge = p('edge, new EdgeSettings)

  def createPlatform(pos: Vec3, rng: Random): Spatial = {
    val platform = ShapeUtils.createPlatform(pos, edge(), topSurface(), bottomSurface(), rng)

    // Add to platforms when generated
    Context.platforms.add(platform)

    platform
  }



}