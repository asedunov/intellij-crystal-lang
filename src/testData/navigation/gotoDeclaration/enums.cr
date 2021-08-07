# SEARCH_TEXT: foo
# RESULT: enum Foo
# RESULT: enum Foo in X
# RESULT: enum FooBar
# RESULT: enum FooBar in X

enum Foo
  X
  Y
  Z
end

enum FooBar
  X
  Y
  Z
end

enum Bar
  X
  Y
  Z
end

module X
  enum Foo
    X
    Y
    Z
  end

  enum FooBar
    X
    Y
    Z
  end

  enum Bar
    X
    Y
    Z
  end
end