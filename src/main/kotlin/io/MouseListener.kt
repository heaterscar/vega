package io

import core.GLFWWindow
import imgui.ImGui
import org.joml.Vector4d
import org.lwjgl.glfw.GLFW
import java.util.*

object MouseListener {
    var cursorPosX: Double = 0.0
    var cursorPosY: Double = 0.0

    val normalizedX: Double
        get() = 2 * (cursorPosX / GLFWWindow.width) - 1
    val normalizedY: Double
        get() = 2 * (cursorPosY / GLFWWindow.height) - 1

    val cursorOrthoX: Double
        get() = Vector4d(normalizedX, 0.0, 0.0, 1.0)
            .mul(GLFWWindow.currentScene.camera.inverseProjectionMatrix)
            .mul(GLFWWindow.currentScene.camera.inverseViewMatrix).x
    val cursorOrthoY: Double
        get() = Vector4d(0.0, normalizedY, 0.0, 1.0)
            .mul(GLFWWindow.currentScene.camera.inverseProjectionMatrix)
            .mul(GLFWWindow.currentScene.camera.inverseViewMatrix).y

    private val io
        get() = ImGui.getIO()

    internal fun sceneCursorPositionCallback(window: Long, xPos: Double, yPos: Double) {
        cursorPosX = xPos
        cursorPosY = yPos
        GLFWWindow.currentScene.onMouseMove.invoke(MouseEvent(xPos = xPos, yPos = yPos))
    }

    internal fun sceneCursorEnterCallback(window: Long, entered: Boolean) {
        if (entered) GLFWWindow.currentScene.onMouseEnter.invoke(MouseEvent())
        else GLFWWindow.currentScene.onMouseExit.invoke(MouseEvent())
    }

    internal fun sceneMouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        val mouseEvent = MouseEvent(button = button, action = Optional.of(action), mods = mods)
        when (action) {
            GLFW.GLFW_PRESS -> GLFWWindow.currentScene.onMousePress.invoke(mouseEvent)
            GLFW.GLFW_RELEASE -> GLFWWindow.currentScene.onMouseRelease.invoke(mouseEvent)
            else -> {}
        }
    }

    internal fun sceneScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        GLFWWindow.currentScene.onMouseScroll.invoke(MouseEvent(xScrollOffset = xOffset, yScrollOffset = yOffset))
    }

    internal fun imGuiMouseButtonCallback(window: Long, button: Int, action: Int, mods: Int) {
        val mouseDown = BooleanArray(5) { button == it && action != GLFW.GLFW_RELEASE }
        io.setMouseDown(mouseDown)
        if (!io.wantCaptureMouse && mouseDown[1]) ImGui.setWindowFocus(null)
    }

    internal fun imGuiScrollCallback(window: Long, xOffset: Double, yOffset: Double) {
        io.mouseWheelH = io.mouseWheelH + xOffset.toFloat()
        io.mouseWheel = io.mouseWheel + yOffset.toFloat()
    }
}