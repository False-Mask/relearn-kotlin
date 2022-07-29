/**
 *@author ZhiQiang Tu
 *@time 2022/7/29  16:07
 *@signature 我将追寻并获取我想要的答案
 */
fun main() {
    //a()
    //b(1)
    println(feiBo(3,0,1))
}

var a = 0

fun a() {
    println(a++)
    a()
}

tailrec fun b(a: Int) {
    println(a)
    b(a + 1)
}

tailrec fun feiBo(n: Int, a: Int, b: Int): Int {
    if(n == 2){
        return b
    }
    return feiBo(n - 1,b,a+b)
}

