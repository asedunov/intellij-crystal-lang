extend MyModule

class TestClass
    extend MyModule
end

module TestModule
    extend MyModule
end

struct TestStruct
    extend MyModule
end

def testDef
    <error descr="'extend' is not allowed in method/function body">extend MyModule</error>
end

fun testFun
    <error descr="'extend' is not allowed in method/function body">extend MyModule</error>
end