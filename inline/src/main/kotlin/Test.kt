import kotlin.coroutines.intrinsics.*

/**
 *@author ZhiQiang Tu
 *@time 2022/8/1  18:32
 *@signature 我将追寻并获取我想要的答案
 */


inline fun call(block: () -> Unit) {
    println("==I am Call ==")
    block()
}

fun testInline() {
    call {
        println("Hello World")
    }
}


suspend fun main() {
    println(a())

}

suspend fun a(): String {
    return suspendCoroutineUninterceptedOrReturn<String> {
        "-----------------------------------------"
    }
}
