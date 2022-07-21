package core


abstract class Component {
    lateinit var gameObject: GameObject

    abstract fun start()
    abstract fun update(dt : Float)
    fun imgui() {}
}
