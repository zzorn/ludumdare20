package net.zzorn

import com.jme3.app.SimpleApplication
import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.bullet.BulletAppState
import com.jme3.scene.{Spatial, Geometry}
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape
import com.jme3.bullet.control.CharacterControl
import com.jme3.input.KeyInput
import com.jme3.input.controls.{ActionListener, KeyTrigger}
import com.jme3.light.{AmbientLight, DirectionalLight}
import net.zzorn.utils.VectorConversion._
import utils.ShapeUtils
import simplex3d.math.float.functions._
import simplex3d.math.float._
import com.jme3.asset.plugins.FileLocator

/**
 * 
 */
object Ludum20 extends SimpleApplication {

  //val bulletAppState = new BulletAppState()

  private var left = false
  private var right = false
  private var up = false
  private var down = false


  def main(args: Array[ String ])
  {
    println("It's dangerous to go Alone! Take this!")

    setShowSettings(false)

    start()
  }

  private var level: Level = new Level()
  private var player: Spatial = null
  //private var playerControl: CharacterControl = null
  private var levelToLoad: Level = null

  private var levelNode: Spatial = null
  private val walkDirection = new Vector3f()

  def simpleInitApp() {

    assetManager.registerLocator("assets", classOf[FileLocator])


    //stateManager.attach(bulletAppState)

    val editor = new LevelEditor()
    editor.start()

    editor.setLevel(level)

    setupPhysics()

    flyCam.setMoveSpeed(100)

    setupLight()


    loadLevel(level)

    player = setupPlayer()
    rootNode.attachChild(player)
    //Context.physicsState.getPhysicsSpace.add(playerControl);
    val actionListener = createActionListener()

    //setupInput(actionListener)


    flyCam.setDragToRotate(true)
    setPauseOnLostFocus(false)
  }


  override def simpleUpdate(tpf: Float) {
    // Load level if requested
    if (levelToLoad != null) {
      level = levelToLoad
      levelToLoad = null
      startLevel(level)
    }

    // Handle player movement
    val camDir = cam.getDirection.clone().multLocal(0.6f)
    val camLeft = cam.getLeft.clone().multLocal(0.4f)
    walkDirection.set(0, 0, 0)
    if (left)  walkDirection.addLocal(camLeft)
    if (right) walkDirection.addLocal(camLeft.negate())
    if (up)    walkDirection.addLocal(camDir)
    if (down)  walkDirection.addLocal(camDir.negate())
    //playerControl.setWalkDirection(walkDirection)
    //cam.setLocation(playerControl.getPhysicsLocation)
  }


  private def startLevel(level: Level) {

    // Remove old platforms
    Context.platforms.clear()

    if (levelNode != null) {
      rootNode.detachChild(levelNode)
      //Context.physicsState.getPhysicsSpace.removeAll(levelNode)
    }

    levelNode = level.generateSpatial()

    //Context.physicsState.getPhysicsSpace.addAll(levelNode)

    viewPort.setBackgroundColor(level.skyColor)

    rootNode.attachChild(levelNode)

    // Spawn player
    //playerControl.setPhysicsLocation(level.spawnLocation)
    player.setLocalTranslation(level.spawnLocation)

  }


  def loadLevel(newLevel: Level) {
    levelToLoad = newLevel
  }


  def setupPhysics() {
    //stateManager.attach(bulletAppState);
  }

  def setupLight() {

    val al = new AmbientLight()
    al.setColor(new ColorRGBA(0.3f, 0.2f, 0.1f, 1f).multLocal(3f))
    rootNode.addLight(al)

    val dl = new DirectionalLight()
    dl.setColor(new ColorRGBA(0.8f, 0.9f, 1f, 1f).multLocal(1.4f))
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal())
    rootNode.addLight(dl)

    val dl2 = new DirectionalLight()
    dl2.setColor(new ColorRGBA(1f, 0.4f, 0.2f, 1f).multLocal(1.1f))
    dl2.setDirection(new Vector3f(1f, 0.4f, 2f).normalizeLocal())
    rootNode.addLight(dl2)
  }

  def setupInput(actionListener: ActionListener) {
    inputManager.addMapping("Lefts",  new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Ups",    new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Downs",  new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jumps",  new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addListener(actionListener, "Lefts");
    inputManager.addListener(actionListener, "Rights");
    inputManager.addListener(actionListener, "Ups");
    inputManager.addListener(actionListener, "Downs");
    inputManager.addListener(actionListener, "Jumps");
  }

  def setupPlayer(): Spatial = {
    /*
    val capsuleShape = new CapsuleCollisionShape(1f, 3f, 1)
    playerControl = new CharacterControl(capsuleShape, 0.1f)
    playerControl.setJumpSpeed(50)
    playerControl.setFallSpeed(30)
    playerControl.setGravity(30)
    */

    val player = ShapeUtils.createSphere(size = Vec3(2, 3, 2))
    //player.addControl(playerControl)
    player
  }

  def createActionListener(): ActionListener = new ActionListener {
    def onAction(binding: String, value: Boolean, tpf: Float) {
      binding match {
        case "Lefts" => left = value
        case "Rights" => right = value
        case "Downs" => down = value
        case "Ups" => up = value
        case "Jumps" => //player.jump()
        case _ => // Do nothing
      }
    }
  }

}


