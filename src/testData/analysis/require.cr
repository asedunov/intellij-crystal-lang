require "foo"

begin
  require "foo"
end

class TestClass
    <error descr="Can't use 'require' in type body">require "foo"</error>
end

module TestModule
    <error descr="Can't use 'require' in type body">require "foo"</error>
end

struct TestStruct
    <error descr="Can't use 'require' in type body">require "foo"</error>
end

def testDef
  <error descr="Can't use 'require' in method/function body">require "foo"</error>
end

fun testFun
  <error descr="Can't use 'require' in method/function body">require "foo"</error>
end