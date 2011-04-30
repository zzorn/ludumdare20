package net.zzorn

import com.jme3.app.SimpleApplication
import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.bullet.BulletAppState
import com.jme3.scene.{Spatial, Geometry}

/**
 * 
 */
object Ludum20 extends SimpleApplication {

  private val bulletAppState = new BulletAppState();

  def main(args: Array[ String ])
  {
    println("It's dangerous to go Alone! Take this!")

    setShowSettings(false)

    start()
  }

  private var level: Level = new Level()
  private var levelToLoad: Level = null

  private var levelNode: Spatial = null

  def simpleInitApp() {

    val editor = new LevelEditor()
    editor.start()

    editor.setLevel(level)

    setupPhysics()

    setupSky()

    flyCam.setMoveSpeed(100)

    setupLight()

    setupInput()

    setupLandscape()

    loadLevel(level)

    flyCam.setDragToRotate(true)
    setPauseOnLostFocus(false)
  }


  override def update() {
    super.update()

    // Load level if requested
    if (levelToLoad != null) {
      level = levelToLoad
      levelToLoad = null
      startLevel(level)
    }
  }

  private def startLevel(level: Level) {
    if (levelNode != null) {
      rootNode.detachChild(levelNode)
    }

    levelNode = level.generateSpatial()

    rootNode.attachChild(levelNode)
  }


  def loadLevel(newLevel: Level) {
    levelToLoad = newLevel
  }


  def setupPhysics() {
    stateManager.attach(bulletAppState);
  }

  def setupSky() {
    viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
  }

  def setupLight() {

  }

  def setupInput() {


  }

  def setupLandscape() {

  }

}


