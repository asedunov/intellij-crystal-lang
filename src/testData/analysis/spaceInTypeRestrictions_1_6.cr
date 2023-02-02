# LANGUAGE_LEVEL: 1.6

def foo1(a<error descr="Space is missing before/after colon">:</error>Int)
end

def foo2(a <error descr="Space is missing before/after colon">:</error>Int)
end

def foo3(a<error descr="Space is missing before/after colon">:</error> Int)
end

def foo4(a : Int)
end

def foo5(a<error descr="Space is missing before/after colon">:</error>(Int))
end

lib L
  $a : Int
end

begin
rescue e : Foo
end

a1: Int

a2 : Int

def foo6(): Int
  1
end

def foo7() : Int
  1
end

def foo8(&b: Int)

end

def foo9(&b : Int)

end