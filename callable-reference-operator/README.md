# ::操作符

::操作符可以干嘛?或者说是用来干什么的?

> **它是用于指向一个已经定义的函数或者属性的**.

## 引用函数

::可引用的函数分为两类

- top-level即顶层函数
- 成员函数

### 顶层函数

```kotlin
fun main() {
    val function = ::function
    function.invoke()
}

fun function() {

}
```

#### 产物

这一看不知道的还以为是lambda(你说他是lambda也没啥问题)
![img_1.png](img_1.png)

#### 实现原理

首先还得是反编译啊
![img_2.png](img_2.png)

还是看字节码吧

```
public final static main()V
   L0
    #获取静态变量TestKt$main$function$1.INSTANCE
    GETSTATIC TestKt$main$function$1.INSTANCE : LTestKt$main$function$1;
    ASTORE 0
   L1
    ALOAD 0
    #强转为kotlin的lambda类型接口
    CHECKCAST kotlin/jvm/functions/Function0
    INVOKEINTERFACE kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; (itf)
    POP
   L2
    RETURN
   L3
    LOCALVARIABLE function Lkotlin/reflect/KFunction; L1 L3 0
```

匿名类
![img_3.png](img_3.png)
![img_4.png](img_4.png)

所以代码总结下来就等价于

```kotlin
fun main() {
    val function = {
        function()
    }
    function.invoke()
}

fun function() {

}
```

好像和lambda的实现是类似的

### 成员函数

```kotlin
fun main() {
    val a = A()
    val function = a::func
}

class A {
    fun func() {

    }
}
```

#### 产物

可以发现和前面的实现类似多生成了一个匿名类.

![img_5.png](img_5.png)

不过也有一定的差异多了一个receiver,而且也没有采用单例了.
不过实现也是类似的，相似度特别高
![img_6.png](img_6.png)

#### 实现原理

```
  public final static main()V
   L0
    LINENUMBER 13 L0
    NEW A               #实例化A对象
    DUP                 #复制栈顶元素
    INVOKESPECIAL A.<init> ()V      #调用构造
    ASTORE 0
   L1
    LINENUMBER 14 L1
    NEW TestKt$main$function$1      #new匿名类
    DUP                         
    ALOAD 0
    INVOKESPECIAL TestKt$main$function$1.<init> (LA;)V      #调用构造
    ASTORE 1
   L2
    LINENUMBER 15 L2
    ALOAD 1
    CHECKCAST kotlin/jvm/functions/Function0                #强转
    #调用接口
    INVOKEINTERFACE kotlin/jvm/functions/Function0.invoke ()Ljava/lang/Object; (itf)
    POP
   L3
    LINENUMBER 16 L3
    RETURN
```

所以等价的代码如下

```kotlin
fun main() {
    val a = A()
    val function = (Function0<Unit>) TestKt $main$function$1(a)
    function.invoke()
}
```

## 小结

- 对于函数 ::符号的方式就是生成匿名类
  > 值得注意的是—— ::操作符可以指向顶层函数和成员函数.
  > 不过对于顶层函数,这个function是个饿汉式单例,而对于成员函数没有使用单例,使用的时候就即时new.
- 生成的匿名类会实现Function接口(也就是高阶函数的接口)
  > 除此之外还会继承一个抽象类,而这个抽象类是kotlin的反射扩展,这边不做解释
- 而invoke方法会调用我们指定需要指向的函数
