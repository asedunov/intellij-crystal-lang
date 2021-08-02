def foo(<error descr="Duplicated parameter name: a">a</error>, b : Int32, <error descr="Duplicated parameter name: a">a</error> : String)
end

def foo(a, b : Int32, c : String)
end

def foo(x a, y b : Int32, z c : String)
end

def foo(x <error descr="Duplicated parameter name: a">a</error>, y b : Int32, z <error descr="Duplicated parameter name: a">a</error> : String)
end

def foo(<error descr="Duplicated external name name: x">x</error> a, y b : Int32, <error descr="Duplicated external name name: x">x</error> c : String)
end

def foo(<error descr="Duplicated external name name: x">"x"</error> a, "y" b : Int32, <error descr="Duplicated external name name: x">x</error> c : String)
end

def foo(<error descr="When specified, external name must be different than internal nam">a</error> a, y b : Int32, z c : String)
end

def foo(x a, <error descr="When specified, external name must be different than internal nam">"b"</error> b : Int32, z c : String)
end