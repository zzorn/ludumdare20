package net.zzorn.utils

import com.jme3.asset.{AssetInfo, AssetLoader}
import org.scalaprops.parser.JsonBeanParser
import java.io.{InputStreamReader, BufferedInputStream}
import net.zzorn.Context

/**
 * Loads beans from json formatted input file.
 */
class BeanLoader extends AssetLoader {
  def load(assetInfo: AssetInfo): Object = {
    val reader = new InputStreamReader(assetInfo.openStream)

    
    val bean = Context.beanParser.parse(reader, assetInfo.getKey.getName)

    reader.close()

    bean
  }
}       



