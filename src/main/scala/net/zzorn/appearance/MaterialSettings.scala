package net.zzorn.appearance

import net.zzorn.utils.VectorConversion._
import util.Random
import org.scalaprops.Bean
import com.jme3.material.Material
import net.zzorn.Context
import com.jme3.math.ColorRGBA
import net.zzorn.Settings
import net.zzorn.utils.MathUtils

/**
 * 
 */
class MaterialSettings extends Settings {

  val color = p('color, new ColorSettings())
  val ambientColor = p('ambientColor, new ColorSettings())
  val specularColor = p('specularColor, new ColorSettings())
  val shininess = p('shininess, 1f).editor(makeSlider(0, 20))
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