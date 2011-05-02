package net.zzorn

import appearance.ShapeUtils
import com.jme3.app.SimpleApplication
import com.jme3.scene.shape.Box
import com.jme3.material.Material
import com.jme3.math.{ColorRGBA, Vector3f}
import com.jme3.bullet.BulletAppState
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
import utils.BeanLoader
import com.jme3.scene.{Node, Spatial, Geometry}
import com.jme3.bounding.BoundingSphere

/**
 * 
 */
// TODO: Extract input handling & player control
object Ludum20 extends SimpleApplication {

  private var left = false
  private var right = false
  private var up = false
  private var down = false
  private var jump = false
  private var wasToggleEditMode = false
  private var toggleEditMode = false
  private var editMode = false

  private var editor: SettingsEditor = null
  private var chaseCam: ChaseCamera = null

  private var ambientLight: AmbientLight = null
  private var directionalLight1: DirectionalLight = null
  private var directionalLight2: DirectionalLight = null
  private var directionalLight3: DirectionalLight = null

  private var player: Spatial = null
  private var playerSteering: Steerable = null
  private var currentLevel: Level = null
  private var nextLevel: Level = null

  private var levelNode: Spatial = null

  def main(args: Array[ String ])
  {
    println("It's dangerous to go Alone! Take this!")

    // TODO: Enable resolution selection before release
    //setShowSettings(false)

    start()
  }

  def simpleInitApp() {
    // Config ways to get assets
    assetManager.registerLocator("assets", classOf[FileLocator])
    assetManager.registerLoader(classOf[BeanLoader], "json")

    // Init lights
    setupLight()

    // Load settings
    println("Loading settings")
    Context.settings = assetManager.loadAsset("config/settings.json").asInstanceOf[GameSettings]
    if (Context.settings == null) {
      println("  Failed to load settings, using defaults")
      Context.settings = new GameSettings()
    }

    // Load the level
    currentLevel = Context.settings.level01()
    loadLevel(currentLevel)

    // Create player
    player = setupPlayer()
    rootNode.attachChild(player)

    // Setup inputs
    val actionListener = createActionListener()
    setupInput(actionListener)

    // Setup camera & editmode
    //chaseCam = new ChaseCamera(cam, player, inputManager);
    flyCam.setMoveSpeed(100)
    updateEditModeStatus()
  }


  private def updateEditModeStatus() {
    if (editMode) {
      println("Activating editor")
      // Enable fly cam
      flyCam.setEnabled(true)
      //chaseCam.setEnabled(false)

      flyCam.setDragToRotate(true)

      setPauseOnLostFocus(false)

      if (editor == null) {
        editor = new SettingsEditor()
        editor.start()
        editor.setSettings(Context.settings)
      }
      editor.setActive(true)
    }
    else {
      println("Activating gamemode")
      if (editor != null) editor.setActive(false)

      // Enable chase cam
      //chaseCam.setEnabled(true)
      //flyCam.setDragToRotate(false)
      //flyCam.setEnabled(false)

      setPauseOnLostFocus(true)
      setCameraToLookAtPlayer()
    }
  }

  override def simpleUpdate(tpf: Float) {
    // Load level if requested
    if (nextLevel != null) {
      currentLevel = nextLevel
      startLevel(currentLevel)
      nextLevel = null
    }

    // Update platform bounding area
    Context.updatePlatformBounds()

    // Handle player movement
    if (playerSteering != null) {
      val camDir: Vec3 = cam.getDirection
      val camLeft: Vec3 = cam.getLeft
      playerSteering.heading := camDir
      playerSteering.leftDir := camLeft
      playerSteering.steeringMovement := Vec3.Zero
      if (left)  playerSteering.steeringMovement += Vec3.UnitZ
      if (right) playerSteering.steeringMovement -= Vec3.UnitZ
      if (up)    playerSteering.steeringMovement += Vec3.UnitX
      if (down)  playerSteering.steeringMovement -= Vec3.UnitX
      playerSteering.jump = jump

      // Toggle edit mode
      // Toggle on key released, hacky approach
      if (toggleEditMode) wasToggleEditMode = true
      if (wasToggleEditMode && !toggleEditMode) {
        wasToggleEditMode = false
        editMode = !editMode
        updateEditModeStatus()
      }
    }

    val playerWalker = player.getControl(classOf[WalkerControl])
    if (playerWalker != null) {
      playerWalker.heading := cam.getDirection

      // Check for reaching goal
      val plat = playerWalker.currentPlatform
      if (plat != null && plat.goal) {
        println("Reached goal")

        // Next level
        // TODO: Fix ugliness
        val next: Level = if (currentLevel == Context.settings.level01()) Context.settings.level02()
        else if (currentLevel == Context.settings.level02()) Context.settings.level03()
        else if (currentLevel == Context.settings.level03()) Context.settings.level04()
        else if (currentLevel == Context.settings.level04()) Context.settings.level05()
        else if (currentLevel == Context.settings.level05()) Context.settings.level01()
        else Context.settings.level01()
        loadLevel(next)
      }

    }

    cam.setLocation(player.getLocalTranslation)

    // Check for fall-death
    if (player.getLocalTranslation.y < currentLevel.killDepth()) {
      println("Fell to death")
      // TODO: Fade to black, then fade back
      startLevel(currentLevel)
    }
  }

  def spawnPlayer() {
    // Place at spawn
    // TODO: Place at last savepoint
    player.setLocalTranslation(currentLevel.spawnLocation)

    // Reset physics -> stop falling
    player.getControl(classOf[WalkerControl]).reset()

    // Move camera
    if (!editMode) {
      setCameraToLookAtPlayer()
    }
  }

  def setCameraToLookAtPlayer() {
    cam.setLocation(new Vector3f(player.getWorldTranslation) + Vec3(-10, 0, 0))
    cam.lookAt(player.getWorldTranslation,Vector3f.UNIT_Y)
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
    spawnPlayer()

    level.configLights(ambientLight,
                       directionalLight1,
                       directionalLight2,
                       directionalLight3)
  }


  def loadLevel(newLevel: Level) {
    nextLevel = newLevel
  }

  def reloadGame(settings: GameSettings) {
    Context.settings = settings
    nextLevel = Context.settings.level01()
  }


  def setupLight() {
    ambientLight = new AmbientLight()
    rootNode.addLight(ambientLight)

    directionalLight1 = new DirectionalLight()
    rootNode.addLight(directionalLight1)

    directionalLight2 = new DirectionalLight()
    rootNode.addLight(directionalLight2)

    directionalLight3 = new DirectionalLight()
    rootNode.addLight(directionalLight3)
  }

  def setupInput(actionListener: ActionListener) {
    inputManager.addMapping("Lefts",  new KeyTrigger(KeyInput.KEY_A));
    inputManager.addMapping("Rights", new KeyTrigger(KeyInput.KEY_D));
    inputManager.addMapping("Ups",    new KeyTrigger(KeyInput.KEY_W));
    inputManager.addMapping("Downs",  new KeyTrigger(KeyInput.KEY_S));
    inputManager.addMapping("Jumps",  new KeyTrigger(KeyInput.KEY_SPACE));
    inputManager.addMapping("Edit",   new KeyTrigger(KeyInput.KEY_F1));
    inputManager.addListener(actionListener, "Lefts");
    inputManager.addListener(actionListener, "Rights");
    inputManager.addListener(actionListener, "Ups");
    inputManager.addListener(actionListener, "Downs");
    inputManager.addListener(actionListener, "Jumps");
    inputManager.addListener(actionListener, "Edit");
  }

  def setupPlayer(): Spatial = {
    val player = new Node()
    player.setModelBound(new BoundingSphere(Context.settings.player().radius(), Vector3f.ZERO))
    //ShapeUtils.createBox(size = Vec3(Context.settings.player().radius(),
    //                                              Context.settings.player().height(),
    //                                              Context.settings.player().radius()))
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
        case "Edit" => toggleEditMode = value
        case _ => // Do nothing
      }
    }
  }

}


