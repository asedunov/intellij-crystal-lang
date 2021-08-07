# SEARCH_TEXT: foo
# RESULT: method foo
# RESULT: method foo in C
# RESULT: method fooBar
# RESULT: method fooBar in C

def foo
end

def bar
end

def fooBar
end

class C
  def foo
  end

  def bar
  end

  def fooBar
  end
end