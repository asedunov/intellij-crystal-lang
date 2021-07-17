include MyModule

class TestClass
    include MyModule
end

module TestModule
    include MyModule
end

struct TestStruct
    include MyModule
end

def testDef
    <error descr="'include' is not allowed in method/function body">include MyModule</error>
end

fun testFun
    <error descr="'include' is not allowed in method/function body">include MyModule</error>
end

lib TestLib
    struct TestStruct
        include MyModule
    end
end