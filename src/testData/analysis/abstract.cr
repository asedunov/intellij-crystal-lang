abstract def myDef

abstract class MyClass
end

abstract struct MyStruct
end

class TestClass
    abstract def myDef

    abstract class MyClass
    end

    abstract struct MyStruct
    end
end

module TestModule
    abstract def myDef

    abstract class MyClass
    end

    abstract struct MyStruct
    end
end

struct TestStruct
    abstract def myDef

    abstract class MyClass
    end

    abstract struct MyStruct
    end
end

def testDef
    <error descr="'abstract' is not allowed in method/function body">abstract</error> def <error descr="Method definition is not allowed in method/function body">myDef</error>

    <error descr="'abstract' is not allowed in method/function body">abstract</error> class <error descr="Class definition is not allowed in method/function body">MyClass</error>
    end

    <error descr="'abstract' is not allowed in method/function body">abstract</error> struct <error descr="Struct definition is not allowed in method/function body">MyStruct</error>
    end
end

fun testFun
    <error descr="'abstract' is not allowed in method/function body">abstract</error> def <error descr="Method definition is not allowed in method/function body">myDef</error>

    <error descr="'abstract' is not allowed in method/function body">abstract</error> class <error descr="Class definition is not allowed in method/function body">MyClass</error>
    end

    <error descr="'abstract' is not allowed in method/function body">abstract</error> struct <error descr="Struct definition is not allowed in method/function body">MyStruct</error>
    end
end