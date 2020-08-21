package com.example.mac.gltest.L11_Touch

import com.example.mac.gltest.util.Geometry

import com.example.mac.gltest.util.ObjectBuilder
import com.example.mac.gltest.util.VertexArray

/**
 * Created by yijie.ma on 2019/1/23.
 */
class Puck constructor(radius: Float, height: Float, numPointAroundPuck: Int) {

    val POSTION_COMPONENT_COUNT = 3
    var height: Float = 0f
    var radius: Float = 0f
    var vertexArray: VertexArray
    var drawList: List<ObjectBuilder.DrawCommand>


    init {
        val genratedData = ObjectBuilder.createPuck(Geometry.Cylinder(Geometry.Point(0f, 0f, 0f), radius, height), numPointAroundPuck)
        this.radius = radius
        this.height = height

        this.vertexArray = VertexArray(genratedData.mVertexData)

        drawList = genratedData.mDrawList
    }

    fun bindData(colorShaderProgram: ShaderProgram.ColorShaderProgram) {

        vertexArray.setVertexAttribPointer(0, colorShaderProgram.positionAttributeLocation, POSTION_COMPONENT_COUNT, 0)
    }

    fun draw() {
        for (i in 0 until drawList.size) {
            drawList[i].draw()
        }
    }


}