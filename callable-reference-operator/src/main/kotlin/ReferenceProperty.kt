import kotlin.reflect.KProperty0

/**
 *@author ZhiQiang Tu
 *@time 2022/8/4  10:01
 *@signature 我将追寻并获取我想要的答案
 */

fun main() {
    val property:KProperty0<String> = ::p
    println(property)
}


private val p = ""