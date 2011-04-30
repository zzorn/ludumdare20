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

/**
 * 
 */
object Ludum20 extends SimpleApplication {

  val bulletAppState = new BulletAppState()

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
  private var player: CharacterControl = null
  private var levelToLoad: Level = null

  private var levelNode: Spatial = null
  private val walkDirection = new Vector3f()

  def simpleInitApp() {

    val editor = new LevelEditor()
    editor.start()

    editor.setLevel(level)

    setupPhysics()

    flyCam.setMoveSpeed(100)

    //setupLight()


    loadLevel(level)

    player = setupPlayer()
    //rootNode.attachChild(player)
    Context.physicsState.getPhysicsSpace.add(player);
    val actionListener = createActionListener(player)
    setupInput(actionListener)


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
    player.setWalkDirection(walkDirection)
    cam.setLocation(player.getPhysicsLocation)
  }


  private def startLevel(level: Level) {
    if (levelNode != null) {
      rootNode.detachChild(levelNode)
    }

    levelNode = level.generateSpatial()

    viewPort.setBackgroundColor(level.skyColor)

    rootNode.attachChild(levelNode)
  }


  def loadLevel(newLevel: Level) {
    levelToLoad = newLevel
  }


  def setupPhysics() {
    stateManager.attach(bulletAppState);
  }

  def setupLight() {
    val al = new AmbientLight()
    al.setColor(ColorRGBA.White.mult(1.3f))
    rootNode.addLight(al)
    val dl = new DirectionalLight()
    dl.setColor(ColorRGBA.White)
    dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal())
    rootNode.addLight(dl)
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

  def setupPlayer(): CharacterControl = {
    val capsuleShape = new CapsuleCollisionShape(0.25f, 2f, 1)
    val player = new CharacterControl(capsuleShape, 0.1f)
    player.setJumpSpeed(20)
    player.setFallSpeed(30)
    player.setGravity(30)
    player.setPhysicsLocation(new Vector3f(0, 10, 0))
    player
  }

  def createActionListener(player: CharacterControl): ActionListener = new ActionListener {
    def onAction(binding: String, value: Boolean, tpf: Float) {
      binding match {
        case "Lefts" => left = value
        case "Rights" => right = value
        case "Downs" => down = value
        case "Ups" => up = value
        case "Jumps" => player.jump()
        case _ => // Do nothing
      }
    }
  }

}


