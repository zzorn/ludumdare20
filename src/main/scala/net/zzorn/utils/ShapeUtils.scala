package net.zzorn.utils

import com.jme3.material.Material
import com.jme3.app.Application._
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.scene.{Spatial, Geometry}

import simplex3d.math.float.functions._
import simplex3d.math.float._

import VectorConversion._
import net.zzorn.Context
import com.jme3.renderer.queue.RenderQueue.ShadowMode
import com.jme3.scene.shape.{Sphere, Box}

/**
 * 
 */
object ShapeUtils {
  
  def createBox(pos:   inVec3 = Vec3.Zero,
                size:  inVec3 = Vec3.One,
                color: inVec4 = Colors.red): Spatial = {

    val mesh = new Box(pos, size.x, size.y, size.z)
    val geom = new Geometry("box", mesh)
    val mat = new Material(Context.assetManager, "Common/MatDefs/Light/Lighting.j3md")
    mat.setBoolean("UseMaterialColors", true)
    mat.setColor("Ambient", color)
    mat.setColor("Diffuse", color)
    mat.setColor("Specular", ColorRGBA.White)
    mat.setFloat("Shininess", 2)

    mat.setTexture("DiffuseMap", Context.assetManager.loadTexture("textures/dusty_grey.png"));

    geom.setMaterial(mat)

    //geom.setShadowMode(ShadowMode.CastAndReceive)

    geom
  }


  def createSphere(pos:   inVec3 = Vec3.Zero,
                   size:  inVec3 = Vec3.One,
                   color: inVec4 = Colors.blue): Spatial = {

    val mesh = new Sphere(20, 20, 1f)
    val geom = new Geometry("sphere", mesh)
    val mat = new Material(Context.assetManager, "Common/MatDefs/Light/Lighting.j3md")
    mat.setTexture("DiffuseMap", Context.assetManager.loadTexture("textures/dusty_grey.png"));
    mat.setBoolean("UseMaterialColors", true)
    mat.setColor("Diffuse", color)
    mat.setColor("Ambient", color)
    mat.setColor("Specular", color)
    geom.setMaterial(mat)

    //geom.setShadowMode(ShadowMode.CastAndReceive)

    geom.setLocalScale(size)
    geom.setLocalTranslation(pos)

    geom
  }






}