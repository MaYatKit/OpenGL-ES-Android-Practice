package com.example.mac.gltest.util

import android.opengl.GLES20.*

/**
 * Created by yijie.ma on 2018/12/12.
 */

class ObjectBuilder {

    private val FLOATS_PER_VERTEX = 3
    private val vertexData: FloatArray
    private var offset: Int = 0

    private var drawList = arrayListOf<DrawCommand>()

    private constructor(sizeInVertices: Int) {
        vertexData = FloatArray(FLOATS_PER_VERTEX * sizeInVertices)
    }


    interface DrawCommand {
        fun draw()
    }


    fun build(): GenratedData {
        return GenratedData(this.vertexData, this.drawList)
    }


    companion object {

        class GenratedData(vertexData: FloatArray, drawList: ArrayList<DrawCommand>) {

            val mVertexData: FloatArray = vertexData
            val mDrawList: ArrayList<DrawCommand> = drawList


        }

        fun sizeOfCircleOfVertices(numPoints: Int): Int {
            return 1 + (numPoints + 1)
        }


        fun sizeOfOpenCylinderInVertices(numPoints: Int): Int {
            return (numPoints + 1) * 2
        }


        fun createPuck(puck: Geometry.Cylinder, numPoints: Int): GenratedData {

            val size = sizeOfCircleOfVertices(numPoints) + sizeOfOpenCylinderInVertices(numPoints)

            val builder = ObjectBuilder(size)

            val puckTop = Geometry.Circle(puck.center.translateY(puck.height / 2f), puck.radius)

            builder.appendCircle(puckTop, numPoints)
            builder.appendOpenCylinder(puck, numPoints)

            return builder.build()
        }


        fun createMallet(center: Geometry.Point, radius: Float, height: Float, numPoints: Int): GenratedData {
            val size = sizeOfCircleOfVertices(numPoints) * 2 + sizeOfOpenCylinderInVertices(numPoints) * 2

            val builder = ObjectBuilder(size)

            val baseHeight = height * 0.25f

            val baseCircle = Geometry.Circle(center.translateY(-baseHeight), radius)
            val baseCylinder = Geometry.Cylinder(baseCircle.center.translateY(-baseHeight / 2f), radius, baseHeight)

            builder.appendCircle(baseCircle, numPoints)
            builder.appendOpenCylinder(baseCylinder, numPoints)


            val handleHeight = height * 0.75f
            val handleRadius = radius / 3f

            val handleCircle = Geometry.Circle(center.translateY(height * 0.5f), handleRadius)
            val handleCylinder = Geometry.Cylinder(handleCircle.center.translateY(-handleHeight / 2f), handleRadius, handleHeight)

            builder.appendCircle(handleCircle, numPoints)
            builder.appendOpenCylinder(handleCylinder, numPoints)
            return builder.build()
        }

    }


    fun appendCircle(circle: Geometry.Circle, numPoints: Int) {
        val startVertex: Int = offset / FLOATS_PER_VERTEX
        val numVertices: Int = sizeOfCircleOfVertices(numPoints)

        //扇形的中心点
        vertexData[offset++] = circle.center.x
        vertexData[offset++] = circle.center.y
        vertexData[offset++] = circle.center.z

        //绕着中心点的扇形。之所以用小于等于是因为要用两次最初的点去造一个扇形
        for (i: Int in 0..numPoints) {
            val angleInRadians: Float = (i / numPoints.toFloat()) * (Math.PI * 2.0f).toFloat()

            vertexData[offset++] = circle.center.x + circle.radius * Math.cos(angleInRadians.toDouble()).toFloat()
            vertexData[offset++] = circle.center.y
            vertexData[offset++] = circle.center.z + circle.radius * Math.sin(angleInRadians.toDouble()).toFloat()
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_FAN, startVertex, numVertices)
            }
        })
    }


    fun appendOpenCylinder(cylinder: Geometry.Cylinder, numPoints: Int) {
        val startVertex = offset / FLOATS_PER_VERTEX
        val numVertices = sizeOfOpenCylinderInVertices(numPoints)

        val yStart = cylinder.center.y - (cylinder.height / 2f)
        val yEnd = cylinder.center.y + (cylinder.height / 2f)

        for (i: Int in 0..numPoints) {
            val angleInRadius: Float = (i / numPoints.toFloat()) * (Math.PI * 2f).toFloat()

            var xPosition: Float = cylinder.center.x + cylinder.radius * Math.cos(angleInRadius.toDouble()).toFloat()
            var zPosition: Float = cylinder.center.z + cylinder.radius * Math.sin(angleInRadius.toDouble()).toFloat()

            vertexData[offset++] = xPosition
            vertexData[offset++] = yStart
            vertexData[offset++] = zPosition


            vertexData[offset++] = xPosition
            vertexData[offset++] = yEnd
            vertexData[offset++] = zPosition
        }

        drawList.add(object : DrawCommand {
            override fun draw() {
                glDrawArrays(GL_TRIANGLE_STRIP, startVertex, numVertices)
            }
        })
    }


}