/**
 *@author ZhiQiang Tu
 *@time 2022/8/7  18:19
 *@signature 我将追寻并获取我想要的答案
 */

fun interface SAM {
    fun say(a: String): Unit
}

fun main() {

    samTest {
        println(it)
    }

}

fun samTest(sam: SAM) {
    sam.say("Hello")
}
