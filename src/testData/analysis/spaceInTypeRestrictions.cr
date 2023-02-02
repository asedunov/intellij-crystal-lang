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