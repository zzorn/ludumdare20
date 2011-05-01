package net.zzorn.appearance

import net.zzorn.appearance.ColorSettings
import net.zzorn.utils.VectorConversion._
import util.Random
import org.scalaprops.Bean
import com.jme3.material.Material
import net.zzorn.Context
import com.jme3.math.ColorRGBA

/**
 * 
 */
class MaterialSettings extends Bean {

  val color = p('color, new ColorSettings(_name = "Material Color"))
  val ambientColor = p('ambientColor, new ColorSettings(0, 0, 0.5f,"Ambient Color"))
  val specularColor = p('specularColor, new ColorSettings(0, 0, 1f,"Specular Color"))
  val shininess = p('shininess, 1f)
  val texture = p('texture, "dusty_grey.png")

  def createMaterial(random: Random = new Random()): Material =  {
    val shader = "Common/MatDefs/Light/Lighting.j3md"
    val mat = new Material(Context.assetManager, shader)
    val loadedTexture = Context.assetManager.loadTexture("textures/" + texture)
    if (loadedTexture != null) mat.setTexture("DiffuseMap", loadedTexture);
    mat.setBoolean("UseMaterialColors", true)
    mat.setColor("Ambient", ambientColor().createColor(random))
    mat.setColor("Diffuse", color().createColor(random))
    mat.setColor("Specular", specularColor().createColor(random))
    mat.setFloat("Shininess", shininess())
    mat
  }
}