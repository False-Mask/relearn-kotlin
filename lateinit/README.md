# lateinit实现原理

- lateinit就变量定义上于普通变量一致
- lateinit是将变量的初始化延后,也就是说可以不立即对变量进行初始化,他具体的实现是通过对方法的get以及调用处进行值类型的判空处理


```kotlin
fun main() {
    val test = Test()
    test.cur
}

class Test {
    lateinit var cur: String

    fun printCur() {
        println(cur)
    }
}
```

就事论事分析main函数
```
L0
    LINENUMBER 8 L0  #源代码行号标记
    NEW Test         #new Test类的实例压入操作数栈
    DUP              #复制栈顶元素，方便后续invoke消耗
    INVOKESPECIAL Test.<init> ()V #弹栈顶元素,初始化对象
    ASTORE 0                      #元素存入局部变量表
L1
    LINENUMBER 9 L1  #..
    ALOAD 0          #局部变量表to操作数栈
    INVOKEVIRTUAL Test.getCur ()Ljava/lang/String; #调用get方法
    POP              #弹栈(没有使用。
L2
    LINENUMBER 10 L2
    RETURN           #方法返回
```
可以发现特别的中规中矩

## lateinit定义分析

```
public Ljava/lang/String; cur
```


## getCur

```
L0
    LINENUMBER 13 L0    #标记行号
    ALOAD 0             #加载局部变量表为0的位置(this)
    GETFIELD Test.cur : Ljava/lang/String; #获取元素值
    DUP                 #复制栈顶元素
    IFNONNULL L1        #判断值是否为null
    LDC "cur"           #为空就将常量池种cur入栈
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.throwUninitializedPropertyAccessException (Ljava/lang/String;)V  
    #调用内部的静态方法
L1
    ARETURN
    #返回

```

所以等价的代码也就是
```kotlin
fun getCur():String{
    if(this.cur == null){
        Intrinsics.throwUninitializedPropertyAccessException("cur")
    }
    return cur
}
```

## setCur

```
L0
    ALOAD 1         #加载局部变量(方法传入的参数
    LDC "<set-?>"   #压入常量池的常量
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
    #检测函数的参数
L1
    LINENUMBER 13 L1
    ALOAD 0         #加入this
    ALOAD 1         #加入方法参数
    PUTFIELD Test.cur : Ljava/lang/String; #设置值
    RETURN          #return;
```

代码逻辑没有发生变化


## printCur

```
public final printCur()V
   L0
    LINENUMBER 16 L0
    ALOAD 0         #压入this
    GETFIELD Test.cur : Ljava/lang/String;  #获取cur的值
    DUP             #复制值
    IFNONNULL L1    #判空
    LDC "cur"       #压入常量
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.throwUninitializedPropertyAccessException (Ljava/lang/String;)V
    #如果为空就throw
   L1
    ASTORE 1
    #非空存入局部变量表
   L2
    #这边就不分析了就System.out.println(cur)
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 1
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L3
   L4
    LINENUMBER 17 L4
    RETURN          #return
   L5
    LOCALVARIABLE this LTest; L0 L5 0
    MAXSTACK = 2
    MAXLOCALS = 2
```
可以发现lateinit对于内部的调用出自动加入了判空处理

等价于
```kotlin
    if(this.cur == null){
        Intrinsics.throwUninitializedPropertyAccessException("cur")
    }
    System.out.println(cur)
```


## 小结
lateinit的实现比较简单。
- 首先依托于kotlin的属性获取,外部对象访问内部对象是通过使用getter方法获取的,即使你的属性是public,他最后生成的代码也不会贸然调用getfield指令。
- 在上述的确保下,只要能保证成员内部调用是安全的,外部就自然而然能确保
- 而确保内部获取属性一定为非空很简单,就是对内部的获取属性的位置进行assert,一旦你获取属性就自行生成一段Intrinsics.throwUninitializedPropertyAccessException这样就确保了值一定非空



最后可以发现其实lateinit和java的普通变量是类似的,要说不同的话lateinit是fail-fast风格的,一旦调用就会assert。
lateinit没有使得kotlin是null安全的,使用不当还是会造成异常,只不过从null pointer转化到了UninitializedPropertyAccessException,所以还是需要慎用。


相对而言声明可空类型要安全不少,不过呢判空处理很是让人头大,开发者应该根据自己的需要自行判断。
- 如果能确保这个类一定不为空,在定义成员变量的时候可以声明为lateinit来延时初始化.

- 如果不可以确保是否为null那最好还是声明为可空类型,通过?. ?: !!操作符判断
