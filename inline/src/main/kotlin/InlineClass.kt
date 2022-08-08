/**
 *@author ZhiQiang Tu
 *@time 2022/8/8  16:03
 *@signature 我将追寻并获取我想要的答案
 */

@JvmInline
value class InlineClass(
    private val value: Int
) {
    fun print() {
        println(value)
    }

    fun value(): Int {
        return value
    }
}

fun main() {
    val x = InlineClass(1)
    println(x.value())
    x.print()
}