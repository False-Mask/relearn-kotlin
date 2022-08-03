/*fun function() {

}
fun main() {
    val function1 = ::function
   function1.invoke()
}*/



fun main() {

    val a = A()
    val function = a::func
    function.invoke()
}


class A {
    fun func() {
        println("AAAA")
    }
}