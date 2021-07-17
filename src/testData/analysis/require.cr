require "foo"

class TestClass
    require "foo"
end

module TestModule
    require "foo"
end

struct TestStruct
    require "foo"
end

def testDef
  <error descr="'require' is not allowed in method/function body">require "foo"</error>
end

fun testFun
  <error descr="'require' is not allowed in method/function body">require "foo"</error>
end