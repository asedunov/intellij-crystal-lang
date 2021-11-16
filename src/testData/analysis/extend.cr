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
    <error descr="Can't use 'extend' in method/function body">extend MyModule</error>
end

fun testFun
    <error descr="Can't use 'extend' in method/function body">extend MyModule</error>
end