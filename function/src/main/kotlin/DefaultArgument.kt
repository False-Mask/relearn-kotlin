/**
 *@author ZhiQiang Tu
 *@time 2022/8/8  15:08
 *@signature 我将追寻并获取我想要的答案
 */

fun test(
    a: Byte = 0,
    b: Short,
    c: Int = 1,
    d: Long,
    e: Float = 2f,
    f: Double,
    g: Boolean = false,
) {
}

fun main() {
    test(a = Byte.MAX_VALUE, b = 10, c = Int.MAX_VALUE, d = 11, e = Float.MAX_VALUE, f = 12.0, g = true)
    test(b = 10, c = Int.MAX_VALUE, d = 11, e = Float.MAX_VALUE, f = 12.0, g = true)
    test(b = 10, c = Int.MAX_VALUE, d = 11, e = Float.MAX_VALUE, f = 12.0)
    test(b = 10, d = 11, f = 12.0)
}