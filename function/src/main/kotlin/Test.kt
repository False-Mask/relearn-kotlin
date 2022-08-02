/**
 *@author ZhiQiang Tu
 *@time 2022/8/2  20:51
 *@signature 我将追寻并获取我想要的答案
 */

fun function(block: (Int) -> Unit) {
    println(block.hashCode())
    block(1)
}

fun main() {

    repeat(10) {
        function {}
    }

    repeat(10) {
        function { }
    }

    repeat(10) {
        function { }
    }

}
