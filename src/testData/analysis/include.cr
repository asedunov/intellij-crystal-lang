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
    <error descr="Can't use 'include' in method/function body">include MyModule</error>
end

fun testFun
    <error descr="Can't use 'include' in method/function body">include MyModule</error>
end

lib TestLib
    struct TestStruct
        include MyModule
    end
end