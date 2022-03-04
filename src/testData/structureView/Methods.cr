def foo1
end

def foo2 : Int
end

def foo3()
end

def foo4() : Int
end

def foo5(x : Int)
end

def foo6(x : Int, s : String)
end

def foo7(x : Int) : String
end

def foo8(x : Int, s : String) : String
end

def foo9(x y : Int)
end

def foo10("x" y : Int)
end

def foo11(x : Int = 1)
end

def Int.foo12
end

def self.foo13
end

def foo14 : A::B::C
end

def foo15 : ::A::B::C
end

module MyModule
  def foo
  end
end

class MyClass
  def foo
  end
end

struct MyStruct
  def foo
  end
end

enum MyEnum
  def foo
  end
end