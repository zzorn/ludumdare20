package net.zzorn.appearance

import com.jme3.material.Material
import com.jme3.app.Application._
import com.jme3.math.{ColorRGBA, Vector3f}
import simplex3d.math.float.functions._
import simplex3d.math.float._

import net.zzorn.Context
import com.jme3.renderer.queue.RenderQueue.ShadowMode
import com.jme3.scene.shape.{Sphere, Box}
import net.zzorn.utils.VectorConversion._
import net.zzorn.utils.{RandomUtils, Colors}
import util.Random
import com.jme3.scene.{Mesh, Spatial, Geometry}

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
    mat.setColor("Ambient", color * 0.5f)
    mat.setColor("Diffuse", color)
    mat.setColor("Specular", ColorRGBA.White)
    mat.setFloat("Shininess", 0.3f)

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
    mat.setColor("Ambient", color * 0.5f)
    mat.setColor("Diffuse", color)
    mat.setColor("Specular", ColorRGBA.White)
    mat.setFloat("Shininess", 2)
    geom.setMaterial(mat)

    //geom.setShadowMode(ShadowMode.CastAndReceive)

    geom.setLocalScale(size)
    geom.setLocalTranslation(pos)

    geom
  }




  def createIrregularSphereMesh(rng: Random = new Random(RandomUtils.randomInt),
                                variation: Float = 0.5f): Mesh = {

    val mesh = new Mesh()

    // TODO: vertex manipulation go here

    mesh
  }






}