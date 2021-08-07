# SEARCH_TEXT: foo
# RESULT: union Foo in L
# RESULT: union FooBar in L

lib L
  union Foo
  end

  union FooBar
  end

  union Bar
  end
end