package net.zzorn.appearance

import com.jme3.material.Material
import com.jme3.app.Application._
import simplex3d.math.float.functions._
import simplex3d.math.float._

import net.zzorn.Context
import com.jme3.renderer.queue.RenderQueue.ShadowMode
import com.jme3.scene.shape.{Sphere, Box}
import util.Random
import com.jme3.math.{Vector2f, ColorRGBA, Vector3f}
import com.jme3.util.BufferUtils
import net.zzorn.utils.{NormalCalculator, MathUtils, RandomUtils, Colors}
import net.zzorn.utils.VectorConversion._
import com.jme3.bounding.BoundingBox
import com.jme3.scene._

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


  def createPlatform(pos:   inVec3 = Vec3.Zero,
                     edge:  EdgeSettings = new EdgeSettings,
                     topSurface:  SurfaceSettings = new SurfaceSettings,
                     bottomSurface:  SurfaceSettings = new SurfaceSettings,
                     random: Random = new Random()): Spatial = {

    def seed: Float = random.nextGaussian().toFloat * 100f

    val sides = 20
    val rings = 14
    val size = edge.edgeFunction(seed)

    def makeSide(top: Boolean,
                 height: (Float, Float) => Float,
                 edgeHeight: (Float, Float) => Float,
                 material: Material): Spatial = {
      val sideMesh = createBlobMesh(sides, rings, size , height, edgeHeight, top)
      val sideGeom = new Geometry("platformSide", sideMesh)
      sideGeom.setMaterial(material)
      sideGeom
    }

    val topHeight = topSurface.surfaceFunction(seed)
    val bottomHeight = bottomSurface.surfaceFunction(seed)
    val topMaterial = topSurface.material().createMaterial(random)
    val bottomMaterial = bottomSurface.material().createMaterial(random)

    val platform = new Node("Platform")
    platform.attachChild(makeSide(true, topHeight, topHeight, topMaterial))
    platform.attachChild(makeSide(false, bottomHeight, topHeight, bottomMaterial))
    //geom.setShadowMode(ShadowMode.CastAndReceive)

    platform.setLocalTranslation(pos)

    platform.setModelBound(new BoundingBox())

    // TODO: Add control that allows querying platform height and extent

    platform
  }


  def createBlobMesh(sides: Int = 16,
                     rings: Int = 16,
                     size: (Float) => Float,
                     height: (Float, Float) => Float,
                     edgeHeight: (Float, Float) => Float,
                     upwards: Boolean = true): Mesh = {


    /* Ascii of the layout:
       4 sides, 2 rings example:
       ____
      |\__/|
      ||\/||
      ||/\||
      |/~~\|
       ~~~~
       Only one-sided
     */

    // NOTE: Triangle strips and triangle fans could be used for optimized performance later

    val numQuads = sides * (rings -1) // Center is made of triangles
    val numTriangles = numQuads * 2 + sides // Quads consist of two triangles + Center triangles
    val numIndexes = numTriangles * 3
    val numVertices = (sides * rings + 1) // +1 for center


    val indexes: Array[Int] = new Array[Int](numIndexes)
    val vertices: Array[Vector3f] = new Array[Vector3f](numVertices)
    val texels: Array[Vector2f] = new Array[Vector2f](numVertices)

    var index = 0
    var vertex = 0

    def addTriangle(a: Int, b: Int, c: Int) {
      if (upwards) {
        indexes(index) = c
        indexes(index + 1) = b
        indexes(index + 2) = a
      }
      else {
        indexes(index) = a
        indexes(index + 1) = b
        indexes(index + 2) = c
      }
      index += 3
    }

    def addQuad(a: Int, b: Int, c: Int, d: Int) {
      addTriangle(a, b, c)
      addTriangle(c, d, a)
    }

    def addCenterRing(center: Int, startSide: Int) {
      var side = startSide
      while (side < startSide + sides ) {
        if (side == startSide) {
          addTriangle(center, startSide, startSide + sides - 1)
        }
        else {
          addTriangle(center, side, side - 1)
        }

        side += 1
      }
    }

    def addVertex(v: Int, side: Int, ring: Int) {

      // Calculate polar coordinates for this vertex
      // OPTIMIZE: Non-linear distribution of ring positions, to make triangle areas similar?
      val r = 1.0f * ring / rings
      val s = 1.0f * side / sides
      val angleAroundCenter = - s * MathUtils.Tau

      // Calculate vertex pos
      // For the outermost ring, use the edge height function
      val y = if (ring == rings) edgeHeight(s, r)
              else if (upwards) height(s, r)
              else min(-height(s, r), edgeHeight(s, r)) // Make sure bottom doesn't go higher than top

      val distanceFromCenter = r * size(s)
      vertices(v) = MathUtils.polarToCartesian3(distanceFromCenter, angleAroundCenter, y)

      // Calculate texture pos
      texels(v) = MathUtils.polarToCartesian(r, angleAroundCenter)

    }


    // Add center vertex
    addVertex(vertex, 0, 0)

    // Add triangles around center
    addCenterRing(vertex, vertex + 1)

    vertex += 1

    var ring = 1
    while (ring <= rings) {
      var side = 0
      while (side < sides) {
        addVertex(vertex, side, ring)

        if (ring > 1) {
          // Add surface between this ring and the one inside it
          if (side == 0) {
            addQuad(vertex, vertex + sides - 1, vertex - 1, vertex - sides)
          }
          else {
            addQuad(vertex, vertex - 1, vertex - sides - 1, vertex - sides)
          }
        }

        vertex += 1
        side += 1
      }
      ring += 1
    }


    // Calculate normals
    val normals: Array[Vector3f] = NormalCalculator.calculateNormals(vertices, indexes)

    val mesh = new Mesh()
    mesh.setBuffer(VertexBuffer.Type.Position, 3, BufferUtils.createFloatBuffer(vertices : _*))
    mesh.setBuffer(VertexBuffer.Type.Normal, 3, BufferUtils.createFloatBuffer(normals : _*))
    mesh.setBuffer(VertexBuffer.Type.TexCoord, 2, BufferUtils.createFloatBuffer(texels: _*))
    mesh.setBuffer(VertexBuffer.Type.Index, 1, BufferUtils.createIntBuffer(indexes : _*))
    mesh
  }






}