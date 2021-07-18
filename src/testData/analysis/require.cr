require "foo"

begin
  require "foo"
end

class TestClass
    <error descr="'require' is not allowed in type body">require "foo"</error>
end

module TestModule
    <error descr="'require' is not allowed in type body">require "foo"</error>
end

struct TestStruct
    <error descr="'require' is not allowed in type body">require "foo"</error>
end

def testDef
  <error descr="'require' is not allowed in method/function body">require "foo"</error>
end

fun testFun
  <error descr="'require' is not allowed in method/function body">require "foo"</error>
end