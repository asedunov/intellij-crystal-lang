# SEARCH_TEXT: foo
# RESULT: class Foo
# RESULT: class Foo in X
# RESULT: class FooBar
# RESULT: class FooBar in X

class Foo
end

class FooBar
end

class Bar
end

module X
  class Foo
  end

  class FooBar
  end

  class Bar
  end
end