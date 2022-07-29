# 可空类型的实现原理

- 在字节码层面上可空类型没有做文章,String?和String其实是同一个类型
- 可空类型不同于java的普通类型有两点:
    - 一是有编译器的限定，可空类型不能直接访问**成员变量**和**成员方法**(可空类型的访问要了用?.安全访问,要么使用!!
      强制访问)
    - 二是可空类型的?.和!!以及?:会生成额外的字节码

## 可空类型的定义

```kotlin
val nullable1: String? = ""
val nullable2: String? = null
val notNull: String = ""
``` 

对比字节码

### nullable1

```
LDC ""    #将常量池的内容压入操作数栈
ASTORE 0  #将操作数栈的栈顶元素弹入局部变量表index为0的位置
```

### nullable2

```
ACONST_NULL #将null压入操作数栈
ASTORE 1    #将操作数栈的栈顶元素弹入局部变量表index为0的位置
```

### notNull

```
LDC ""     #将常量池内元素""压入操作数栈
ASTORE 2   #将操作数栈的栈顶元素...index为2
```

### 小结

对比可以发现就定义上其实3中类型的定义都是类似的。
感兴趣的话可以用java进行类似的定义然后进行对比(结果是一致的)

## 可空类型的操作数实现

### ?.

代码如下

```kotlin
fun main() {
    val a: A = A()
    val nullable = a.a?.length
    val notNull = a.b.length
    println(nullable)
    println(notNull)
}

data class A(
    val a: String? = null,
    val b: String = "",
)
```

```
   L1
    LINENUMBER 3 L1 #标记代码行号
    ALOAD 0         #将局部变量表为0引用压入(也就是a)
    INVOKEVIRTUAL A.getA ()Ljava/lang/String; #调用getA,消耗栈顶元素返回a.a的值
    DUP             #复制栈内顶元素
    IFNULL L2       #判断栈顶元素是否为空，如果为空跳转到L2处否则继续执行(其实就是一个if)
    INVOKEVIRTUAL java/lang/String.length ()I #如果栈顶(a.a)非空调用a.a.length方法返回一个int
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer; #对int元素进行装箱
    GOTO L3         #else分支执行完毕goto
   L2
    POP             #如果ifnull满足先将栈顶元素pop
    ACONST_NULL     #然后压入null ??黑人问号??你这是不是有点多余了?栈顶元素为null,你给null弹了然后再入?
   L3
    ASTORE 1        #存放入局部变量表中index为1的位置
   L4
```
根据上面的字节码我们可以对a.a?.length做等价替换
```kotlin
val nullable = if(a.a == null) null else a.a.length()
```

### !!
关于!!是干什么的我想都懂
```kotlin
fun main() {
    val a: A = A()
    val nullable = a.a!!.length
    println(nullable)
}

data class A(
    val a: String? = null,
    val b: String = "",
)

fun definition() {
    val nullable1:String? = ""
    val nullable2:String? = null
    val notNull:String = ""
}
```

字节码如下
```
L1
    LINENUMBER 3 L1  #标记源代码行号
    ALOAD 0          #将局部a压入操作数栈
    INVOKEVIRTUAL A.getA ()Ljava/lang/String; #调用a.a获取成员变量a的值
    DUP                                       #复制栈顶元素的值
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNull (Ljava/lang/Object;)V
    # 调用静态方法Intrinsics.checkNotNull(Object)
    INVOKEVIRTUAL java/lang/String.length ()I #调用a.a.length()
    ISTORE 1
L2
```
明了了等价于
```kotlin
Intrisics.checkNotNull(a.a)
val nullable = a.a.length()
```
总的来说这个!!生成的checkNotNull意义不是很大,可能是遵循fail-fast原则吧
不过总的来说还是为了编译的时候能够通过吧



### ?:

```kotlin
fun main() {
    val a: A = A()
    val nullable = a.a?.length ?: "ABC"
    val notNull = a.b.length
    println(nullable)
    println(notNull)
}

data class A(
    val a: String? = null,
    val b: String = "",
)
```

字节码
```
L1
    LINENUMBER 3 L1
    ALOAD 0
    INVOKEVIRTUAL A.getA ()Ljava/lang/String;
    DUP
    IFNULL L2
    INVOKEVIRTUAL java/lang/String.length ()I
    INVOKESTATIC java/lang/Integer.valueOf (I)Ljava/lang/Integer;
    GOTO L3
L2
    POP
    LDC "ABC"
L3
    ASTORE 1
L4
```
可以发现和上面的!!类似就是L2变化了点
以前是
```
POP       
ACONST_NULL
```
所以等价于

```kotlin
val nullable = if(a.a == null) "ABC" else a.a.lengt()
```
也就是设置了一个默认值而已
