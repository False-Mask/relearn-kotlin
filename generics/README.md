# 泛型

- 泛型又叫参数化类型,可以理解是一种类型参数(一种表征类型的参数类型)
- 泛型**主要**起到的是规范作用,其行为主要是在编译期报错来避免一些潜在的类型安全问题
- kotlin泛型按种类可分为以下3类
  - 函数泛型
  - 类泛型
  - 接口泛型
  - 真泛型类型reified

## 函数泛型

### 作为函数参数传入


```kotlin
fun <T> get(t: T) {
    println(t.toString())
}
```

字节码如下

```
 public final static get(Ljava/lang/Object;)V      
    #这也就是著名的类型擦除,泛型类型最后会被会被编译成Object而没有真正生成具体的类
    // annotable parameter count: 1 (visible)
    // annotable parameter count: 1 (invisible)
   L0
    LINENUMBER 8 L0    #源代码行号
    ALOAD 0            #局部变量表0位置的引用类型存入操作数栈
    INVOKESTATIC java/lang/String.valueOf (Ljava/lang/Object;)Ljava/lang/String;
    #调用静态方法String.valueOf()
    ASTORE 1           #调用结果存入局部变量表index为1的位置
   L1
    #熟悉的sout
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 1
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L2
   L3
    LINENUMBER 9 L3
    RETURN              #return
   L4
    LOCALVARIABLE t Ljava/lang/Object; L0 L4 0  #局部变量表
    MAXSTACK = 2
    MAXLOCALS = 2
```

可以发现如果泛型作为函数的参数是没有任何的运行时提示的.

等价
```kotlin
fun get(t:Any){
    println(t)
}

```

### 作为函数的返回值


```kotlin
fun <T> set(any: Any): T {
    return any as T
}
```

```
 public final static set(Ljava/lang/Object;)Ljava/lang/Object;
    #依然类型擦除,返回值直接被替换为了obj
    // annotable parameter count: 1 (visible)
    // annotable parameter count: 1 (invisible)
    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
   L0
    ALOAD 0     #局部变量入栈(方法参数)
    LDC "any"   #常量池any常量入栈
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
    #类型检测(定义的时候使用的kotlin的不可空类型,所以生成了check)
   L1
    LINENUMBER 12 L1
    ALOAD 0     #invokestatic消耗了栈顶两个元素,目前栈顶为空,继续压入set方法的方法参数
    ARETURN     #直接return
   L2
    LOCALVARIABLE any Ljava/lang/Object; L0 L2 0
    MAXSTACK = 2
    MAXLOCALS = 1
```

有些惊讶,显式地调用as进行类型强制,但是这个as只确保了编译通过,没有生成任何额外地字节码

函数等价于

```kotlin
fun a(any:Any):Any = any
```

### 类型限定

```kotlin
class A {
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
```

constrain1分析
```
public final static constrains1(LA;)V
    #说这是类型擦除吧也是,但是呢你说他不是好像确实也不是.
    #总共不是obj了
    // annotable parameter count: 1 (visible)
    // annotable parameter count: 1 (invisible)
    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
   L0
   #等价Intrinsics.checkNotNullParameter(t);
    ALOAD 0
    LDC "t"
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   L1
    LINENUMBER 22 L1
    #等价于t.sayHello()
    ALOAD 0
    INVOKEVIRTUAL A.sayHello ()V
   L2
    LINENUMBER 23 L2
    RETURN
   L3
    LOCALVARIABLE t LA; L0 L3 0
    MAXSTACK = 2
    MAXLOCALS = 1
```
可以发现加入了类型限定以后类型擦除不是obj,而是限定的那个类


constraint2分析

```
public final static constrains2(Ljava/lang/Object;)LA;
  @Lorg/jetbrains/annotations/NotNull;() // invisible
    // annotable parameter count: 1 (visible)
    // annotable parameter count: 1 (invisible)
    @Lorg/jetbrains/annotations/NotNull;() // invisible, parameter 0
   L0
    ALOAD 0
    LDC "obj"
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   L1
    LINENUMBER 26 L1
    ALOAD 0
   L2
    CHECKCAST A       #与前面的类似只是多了一条cast指令
    ARETURN
   L3
    LOCALVARIABLE obj Ljava/lang/Object; L0 L3 0
    MAXSTACK = 2
    MAXLOCALS = 1
```

### 小结

- 对于函数泛型如果没有类型限制那么最后会被擦除为Object
- 如果有类型限制,那么就会将类型擦除为限制的类型,这时候就会生成cast指令,不留神就会报ClassCastException


## 类泛型

```kotlin
class B<T>(val t: T)
```

```
public final class B {

  private final Ljava/lang/Object; t  #类型擦除为了Object,后续的也没必要看了一个样

  public final getT()Ljava/lang/Object;
   L0
    LINENUMBER 30 L0
    ALOAD 0
    GETFIELD B.t : Ljava/lang/Object;
    ARETURN

  public <init>(Ljava/lang/Object;)V
   L0
    LINENUMBER 30 L0
    ALOAD 0
    INVOKESPECIAL java/lang/Object.<init> ()V
    ALOAD 0
    ALOAD 1
    PUTFIELD B.t : Ljava/lang/Object;
    RETURN
}
```

### in,out

```kotlin

class A {
  fun sayHello() {
    println("Hello I am A")
  }
}

class C<in Generics : A> {
  private var value: Generics? = null

  fun a(a: A) {
    a.sayHello()
    this.value = a as Generics
  }
}

```

```
public final class C {


  // access flags 0x2
  // signature TGenerics;
  // declaration: value extends Generics
  #合理的,由于加入了类型的限定,所以类型绑定为了A
  private LA; value

   L0
    #判空
    #局部变量表0为this,1为a方法的参数
    ALOAD 1
    LDC "a"
    INVOKESTATIC kotlin/jvm/internal/Intrinsics.checkNotNullParameter (Ljava/lang/Object;Ljava/lang/String;)V
   L1
    LINENUMBER 37 L1
    #等价于a.sayHello()
    ALOAD 1
    INVOKEVIRTUAL A.sayHello ()V
   L2
    LINENUMBER 38 L2
    #压入局部变量this和a
    ALOAD 0
    ALOAD 1
   L3
    #设置成员变量
    PUTFIELD C.value : LA;
   L4
    LINENUMBER 39 L4
    RETURN
   L5
    LOCALVARIABLE this LC; L0 L5 0
    LOCALVARIABLE a LA; L0 L5 1
    MAXSTACK = 2
    MAXLOCALS = 2
}
```

可以发现in关键字其实只是编译期间不允许你返回带有对应泛型的返回值,没有新增加任何指令.

类似的out只是不允许你定义任何带有该泛型类型参数的方法
```kotlin
class D<out Generics : A> {
    private var value: Generics? = null

    fun getV(): Generics? {
        return value
    }
}
```


## 接口泛型


```kotlin
interface GenericsInterface<A> {
    fun m(a: A):A
}
```

字节码分析

```
public abstract interface GenericsInterface {
  # 依然是类型擦除
  public abstract m(Ljava/lang/Object;)Ljava/lang/Object;
}
```

类似的in/out 类型限定的实现原理和上述实现类似。不做赘述。


## reified

reified是需要借助inline它是泛型,but它的实现是依靠的inline.
reified = generics + inline


```kotlin
inline fun <reified A> reifiedGenerics(a: A) {
  println(a)
  println(A::class.java)
}

fun main() {
  reifiedGenerics<String>("")
}
```

当你去分析reifiedGenerics来了解reified的实现的时候.
你就走错路了.
因为他是inline,而且强制绑定inline
它的实现你得去调用处进行分析.

```
public final static main()V
   L0
    LINENUMBER 74 L0
    LDC ""
    ASTORE 0
   L1
    ICONST_0
    ISTORE 1
   L2
    LINENUMBER 80 L2
   L3
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 0
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L4
   L5
    LINENUMBER 81 L5
    LDC Ljava/lang/String;.class        #class常量入栈
    ASTORE 2
   L6
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 2
    INVOKEVIRTUAL java/io/PrintStream.println (Ljava/lang/Object;)V
   L7
   L8
    LINENUMBER 82 L8
    NOP
   L9
    LINENUMBER 75 L9
    RETURN
```

代码等价于

```kotlin
fun main(){
    println("")
    println(String::class.java)
}
```

