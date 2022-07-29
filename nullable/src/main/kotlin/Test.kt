fun main() {
    val a: A = A()
    val nullable = a.a?.length ?: "ABC"
    val notNull = a.b.length
    println(nullable)
    println(notNull)
}

data class A(
    val a: String? = null,
    val b: String = "",
)

fun definition() {
    val nullable1:String? = ""
    val nullable2:String? = null
    val notNull:String = ""
}