def foo
end

def foo(x : Int32) : String
end

def foo(x, "b" y)
end

def foo(x = 1)
end

def foo(@x, @@y)
end

private def foo
end

abstract def foo

@[Foo]
def foo
end

module M
  def foo
  end

  module X::Y
    def foo
    end
  end

  module ::X::Y
    def foo
    end
  end
end