# SEARCH_TEXT: foo
# RESULT: struct Foo
# RESULT: struct Foo in L
# RESULT: struct Foo in X
# RESULT: struct FooBar
# RESULT: struct FooBar in L
# RESULT: struct FooBar in X

struct Foo
end

struct FooBar
end

struct Bar
end

module X
  struct Foo
  end

  struct FooBar
  end

  struct Bar
  end
end

lib L
  struct Foo
  end

  struct FooBar
  end

  struct Bar
  end
end