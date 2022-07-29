# 尾递归优化

- 尾递归就是虚伪的递归,他是一种类似于循环的执行流程

## 递归字节码

```kotlin
var a = 0

fun a() {
    println(a++)
    a()
}
```

字节码如下

```
public final static a()V
   L0
    LINENUMBER 15 L0
    GETSTATIC TailrecTestKt.a : I
    DUP
    ISTORE 0
    ICONST_1
    IADD
    PUTSTATIC TailrecTestKt.a : I
    ILOAD 0
    ISTORE 0
   L1
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ILOAD 0
    INVOKEVIRTUAL java/io/PrintStream.println (I)V
   L2
   L3
    LINENUMBER 16 L3
    INVOKESTATIC TailrecTestKt.a ()V  #可以发现在调用完全以后会去调用本身.
   L4
    LINENUMBER 17 L4
    RETURN
```


## 非递归(tailrec)

```kotlin
tailrec fun b(a: Int) {
    println(a)
    b(a + 1)
}
```

字节码如下

```
public final static b(I)V
    // annotable parameter count: 1 (visible)
    // annotable parameter count: 1 (invisible)
   L0
    LINENUMBER 20 L0
   L1
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ILOAD 0
    INVOKEVIRTUAL java/io/PrintStream.println (I)V
   L2
   L3
    LINENUMBER 21 L3
    ILOAD 0
    ICONST_1
    IADD
    ISTORE 0
    GOTO L0   #这里就暴露了，这边是直接用值进行局部变量的更新，但是这没有创建新的栈帧
```

代码等价于

```kotlin
tailrec fun b(a: Int) {
    while(true){
        println(a)
        a++
    }
}
```

类似的斐波那契数列
```kotlin
tailrec fun feiBo(n: Int, a: Int, b: Int): Int {
    if(n == 2){
        return b
    }
    return feiBo(n - 1,b,a+b)
}
```

等价于
```kotlin
fun feiBo(n: Int, a: Int, b: Int): Int {
    while(true) {
        if (n == 2) {
            return b
        }
        val newN = n - 1
        val newA = b
        val newB = a + b
        n = newN
        a = newA
        b = newB
    }
}
```