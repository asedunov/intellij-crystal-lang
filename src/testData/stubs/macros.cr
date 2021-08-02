macro foo
end

macro foo(x, "b" y)
end

macro foo(x = 1)
end

private macro foo
end

@[Foo]
macro foo
end

module M
  macro foo
  end

  module X::Y
    macro foo
    end
  end

  module ::X::Y
    macro foo
    end
  end
end