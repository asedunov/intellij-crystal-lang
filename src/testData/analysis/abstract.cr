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
    <error descr="Can't use 'abstract' in method/function body">abstract</error> def <error descr="Can't declare method in method/function body">myDef</error>

    <error descr="Can't use 'abstract' in method/function body">abstract</error> class <error descr="Can't declare class in method/function body">MyClass</error>
    end

    <error descr="Can't use 'abstract' in method/function body">abstract</error> struct <error descr="Can't declare struct in method/function body">MyStruct</error>
    end
end

fun testFun
    <error descr="Can't use 'abstract' in method/function body">abstract</error> def <error descr="Can't declare method in method/function body">myDef</error>

    <error descr="Can't use 'abstract' in method/function body">abstract</error> class <error descr="Can't declare class in method/function body">MyClass</error>
    end

    <error descr="Can't use 'abstract' in method/function body">abstract</error> struct <error descr="Can't declare struct in method/function body">MyStruct</error>
    end
end