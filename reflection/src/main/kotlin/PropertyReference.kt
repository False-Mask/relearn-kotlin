/**
 *@author ZhiQiang Tu
 *@time 2022/8/7  17:06
 *@signature 我将追寻并获取我想要的答案
 */

class PropertyReference {
    var a = 10
}

fun main() {
    val receiver = PropertyReference()
    val propertyReference = receiver::a
    val a = receiver::a
    a.getter
}