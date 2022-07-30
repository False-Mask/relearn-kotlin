/**
 *@author ZhiQiang Tu
 *@time 2022/7/30  22:47
 *@signature 我将追寻并获取我想要的答案
 */

fun <T> get(t: T) {
    println(t.toString())
}

fun <T> set(any: Any): T {
    return any as T
}

open class A {
    fun sayHello() {
        println("Hello I am A")
    }
}

fun <T : A> constrains1(t: T) {
    t.sayHello()
}

fun <T : A> constrains2(obj: Any): T {
    return obj as T
}


class B<T>(val t: T)


class C<in Generics : A> {
    private var value: Generics? = null

    fun a(a: A) {
        a.sayHello()
        this.value = a as Generics
    }
}


class D<out Generics : A> {
    private var value: Generics? = null

    fun getV(): Generics? {
        return value
    }
}

