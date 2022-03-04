x : Int

y : Int = 1

z : A::B::C = 1

u : ::A::B::C = 1

def foo
  x : Int = 1
end

class A
  x : Int = 1

  @x : Int = 1

  @@x : Int = 1
end