package net.zzorn.utils

import com.jme3.math.Vector3f

/**
 * Calculates normal vectors for a shape.
 *
 * Borrowed from my open source project skycastle:
 * https://github.com/zzorn/skycastle/blob/master/src/main/scala/org/skycastle/util/mesh/NormalCalculator.scala
 */
object NormalCalculator {

  def calculateNormals(vertexes: Array[Vector3f], triangleIndexes: Array[Int] ): Array[Vector3f] = {
    val normals = new Array[Vector3f](vertexes.size)

    // Initialize normals
    var ni = 0
    while (ni < normals.size) {
      normals(ni) = new Vector3f()
      ni += 1
    }

    def setNormal(vertexIndex: Int, normal: Vector3f) {
      normals(vertexIndex).addLocal(normal)
    }

    // Calculate normal for each triangle
    var i = 0
    val sideAB = new Vector3f()
    val sideAC = new Vector3f()
    while (i < triangleIndexes.size) {
      val ai = triangleIndexes(i)
      val bi = triangleIndexes(i + 1)
      val ci = triangleIndexes(i + 2)
      val a = vertexes(ai)
      val b = vertexes(bi)
      val c = vertexes(ci)

      if (a != null && b != null && c != null) {
        b.subtract(a, sideAB)
        c.subtract(a, sideAC)

        val normal = sideAB.cross(sideAC)
        if (normal.lengthSquared > 0) {
          setNormal(ai, normal)
          setNormal(bi, normal)
          setNormal(ci, normal)
        }
      }
      else {
        println("WARNING: Missing vertex in mesh for index "+ ai +", " + bi + ", or " + ci)
      }


      i += 3
    }

    // Normalize
    var normalIndex = 0
    while (i < normals.size) {
      val normal = normals(normalIndex)
      normal.normalizeLocal
      normalIndex += 1
    }

    normals
  }

}