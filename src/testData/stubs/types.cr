def foo(x : self)
end

def foo(x : X)
end

def foo(x : A::B::X)
end

def foo(x : ::A::B::X)
end

def foo(*x : *Int32)
end

def foo(**x : **Int32)
end

def foo(x : {Int32, String})
end

def foo(x : {n: Int32, "s": String})
end

def foo(x : Int32 | String)
end

def foo(x : Foo(_))
end

def foo(x : Int32*)
end

def foo(x : Int32.class)
end

def foo(x : Int32[10])
end

def foo(x : Int32?)
end

def foo(x : typeof(1 + 2))
end

def foo(x : (Int32))
end

def foo(x : (Int32 -> String))
end

def foo(x : Int32, Boolean -> String)
end

def foo(x : -> String)
end

def foo(x : Int32 -> )
end