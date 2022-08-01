# inline

我们都知道内联表示的是`代码内联`这个内联指的就是将代码直接抄过来

即将在调用处把代码copy过去

怎么看？

```kotlin
inline fun call(block: () -> Unit) {
    println("==I am Call ==")
    block()
}

fun testInline() {
    call {
        println("Hello World")
    }
}
```

```
public final static testInline()V
   L0
    LINENUMBER 16 L0
   L1
    ICONST_0
    ISTORE 0
   L2
    LINENUMBER 34 L2
    LDC "==I am Call ==" !!!!!!!!!!!!!!
    ASTORE 1
   L3
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 1
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L4
   L5
    LINENUMBER 35 L5
   L6
    ICONST_0
    ISTORE 2
   L7
    LINENUMBER 17 L7
    LDC "Hello World"
    ASTORE 3
   L8
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 3
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L9
   L10
    LINENUMBER 18 L10
    NOP
   L11
   L12
    LINENUMBER 35 L12
    NOP
   L13
    LINENUMBER 36 L13
    NOP
   L14
    LINENUMBER 19 L14
    RETURN
```

关于crossline和noinline加强内联,一个减弱内联.可自行分析.

# 开胃菜

suspendCoroutineUninterceptedOrReturn在协程中频繁用到.
笔者在学习协程的时候见过n多次这个函数调用,奈何当时能力不够,难于对其有什么理解.

为什么suspendCoroutineUninterceptedOrReturn分析没有头绪呢?
因为函数是空的!!

```kotlin
public suspend inline fun <T> suspendCoroutineUninterceptedOrReturn(crossinline block: (Continuation<T>) -> Any?): T {
    contract { callsInPlace(block, InvocationKind.EXACTLY_ONCE) }
    throw NotImplementedError("Implementation of suspendCoroutineUninterceptedOrReturn is intrinsic")
}
```

Implementation of suspendCoroutineUninterceptedOrReturn is intrinsic
函数实现是内部特性.

空的✌怎么办,好像看不了,对吧?对吗?

对了一半,你看inline!  inline内联函数,内联?分析调用处?妙啊!

```kotlin
suspend fun a(): String {
    return suspendCoroutineUninterceptedOrReturn<String> {
        "-----------------------------------------"
        COROUTINE_SUSPENDED
    }
}
```

这里可以考虑decompile java或者还是class file总体都不难

```
public final static a(Lkotlin/coroutines/Continuation;)Ljava/lang/Object;

   L0
    LINENUMBER 28 L0
    ALOAD 0         #Continuation局部变量入栈
    ASTORE 1        #存入局部变量表为1的位置
   L1
    ICONST_0        #1入栈
    ISTORE 2        #存入index为2的局部变量表
   L2
    LINENUMBER 29 L2
    NOP             #空指令,忽略
   L3
    LINENUMBER 30 L3
    INVOKESTATIC kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
    #获取挂起标记
   L4
   L5
    LINENUMBER 28 L5
    DUP             #复制栈顶
    INVOKESTATIC kotlin/coroutines/intrinsics/IntrinsicsKt.getCOROUTINE_SUSPENDED ()Ljava/lang/Object;
    #获取挂起标记
    IF_ACMPNE L6    #对比栈顶元素即getCOROUTINE_SUSPENDED != 对比栈顶元素即getCOROUTINE_SUSPENDED
    #满足就直接跳转道L6即return处
    #否则需要执行invokestatic指令
    ALOAD 0         
    INVOKESTATIC kotlin/coroutines/jvm/internal/DebugProbesKt.probeCoroutineSuspended (Lkotlin/coroutines/Continuation;)V
   L7
   L6
    LINENUMBER 28 L6
    ARETURN
```

这段代码等价于

```kotlin


fun a(arg: Continuation<String>):Any {
    //suspendCoroutineUninterceptedOrReturn内传入的高阶函数的返回值
    val result:Any
    if(result == COROUTINE_SUSPENDED){
        probeCoroutineSuspended()
    }
    return result 
}
```
可能这个实例不清晰如果是这样,就好看了。
```kotlin
suspend fun a(): String {
    return suspendCoroutineUninterceptedOrReturn<String> {
        "-----------------------------------------"
    }
}
```

简单的来说就是suspendCoroutineUninterceptedOrReturn会一些指令进行内联
除了内联代码以外还生成一个一点点代码
他会把这个高阶函数的返回值与COROUTINE_SUSPENDED进行比对。一致就表明被挂起了,就会调用probeSuspended
(这个调用是用来调试协程的,他的具体实现在debugger里面,点开看源码也是空的)


为什么需要用到suspendCoroutineUninterceptedOrReturn
因为java代码和kotlin无法形容,最最主要的是无法获取Continuation,因为就高阶函数内传入Continuation,无论怎么写在语法层面上都是不合理的,
所以就只能通过internal进行处理(这里的internal**应该是**kotlinc)对字节码做特殊操作.