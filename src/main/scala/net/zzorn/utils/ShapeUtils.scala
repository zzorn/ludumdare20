package net.zzorn.utils

import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.app.Application._
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.{Spatial, Geometry}

import simplex3d.math.float.functions._
import simplex3d.math.float._

import VectorConversion._
import net.zzorn.Context

/**
 * 
 */
object ShapeUtils {
  
  def createBox(pos:   inVec3 = Vec3.Zero,
                size:  inVec3 = Vec3.One,
                color: inVec4 = Colors.red): Spatial = {

    val b = new Box(pos, size.x, size.y, size.z)
    val geom = new Geometry("box", b)
    val mat = new Material(Context.assetManager, "Common/MatDefs/Misc/Unshaded.j3md")
    mat.setColor("Color", color)
    geom.setMaterial(mat)

    geom
  }






}