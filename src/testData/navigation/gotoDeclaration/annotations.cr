# SEARCH_TEXT: foo
# RESULT: annotation Foo
# RESULT: annotation Foo in X
# RESULT: annotation FooBar
# RESULT: annotation FooBar in X

annotation Foo
end

annotation FooBar
end

annotation Bar
end

module X
  annotation Foo
  end

  annotation FooBar
  end

  annotation Bar
  end
end