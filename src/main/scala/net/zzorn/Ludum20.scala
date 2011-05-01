package net.zzorn

import appearance.ShapeUtils
import com.jme3.app.SimpleApplication
import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.bullet.BulletAppState
import com.jme3.scene.{Spatial, Geometry}
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape
import com.jme3.bullet.control.CharacterControl
import com.jme3.input.controls.{ActionListener, KeyTrigger}
import com.jme3.light.{AmbientLight, DirectionalLight}
import controls.{PhysicsSettings, SpeedSettings, WalkerControl, Steerable}
import level.Level
import net.zzorn.utils.VectorConversion._
import simplex3d.math.float.functions._
import simplex3d.math.float._
import com.jme3.asset.plugins.FileLocator
import com.jme3.input.{ChaseCamera, KeyInput}

/**
 * 
 */
// TODO: Extract input handling & player control
object Ludum20 extends SimpleApplication {

  //val bulletAppState = new BulletAppState()

  private var left = false
  private var right = false
  private var up = false
  private var down = false
  private var jump = false


  def main(args: Array[ String ])
  {
    println("It's dangerous to go Alone! Take this!")

    setShowSettings(false)

    start()
  }

  private var level: Level = new Level()
  private var player: Spatial = null
  private var playerSteering: Steerable = null
  private var levelToLoad: Level = null

  private var levelNode: Spatial = null

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

    setupInput(actionListener)


    flyCam.setDragToRotate(true)

    flyCam.setEnabled(false);
    val chaseCam = new ChaseCamera(cam, player, inputManager);

    setPauseOnLostFocus(false)
  }


  override def simpleUpdate(tpf: Float) {
    // Load level if requested
    if (levelToLoad != null) {
      level = levelToLoad
      levelToLoad = null
      startLevel(level)
    }

    // Update platform bounding area
    Context.updatePlatformBounds()

    // Handle player movement
    if (playerSteering != null) {
      val camDir: Vec3 = cam.getDirection.clone().multLocal(0.6f)
      val camLeft: Vec3 = cam.getLeft.clone().multLocal(0.4f)
      playerSteering.steeringMovement := Vec3.Zero
      if (left)  playerSteering.steeringMovement += camLeft
      if (right) playerSteering.steeringMovement -= camLeft
      if (up)    playerSteering.steeringMovement += camDir
      if (down)  playerSteering.steeringMovement -= camDir
      playerSteering.jump = jump
    }

    val playerWalker = player.getControl(classOf[WalkerControl])
    if (playerWalker != null) {
      playerWalker.heading := cam.getDirection
    }
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
    player.getControl(classOf[WalkerControl]).reset()


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
    val player = ShapeUtils.createBox(size = Vec3(Context.settings.player().radius(),
                                                  Context.settings.player().height(),
                                                  Context.settings.player().radius()))
    val playerWalker = new WalkerControl(Context.settings.player())
    //playerWalker.loggingOn = true
    playerSteering = playerWalker
    player.addControl(playerWalker)
    player
  }

  def createActionListener(): ActionListener = new ActionListener {
    def onAction(binding: String, value: Boolean, tpf: Float) {
      binding match {
        case "Lefts" => left = value
        case "Rights" => right = value
        case "Downs" => down = value
        case "Ups" => up = value
        case "Jumps" => jump = value
        case _ => // Do nothing
      }
    }
  }

}


