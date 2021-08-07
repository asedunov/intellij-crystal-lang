# SEARCH_TEXT: foo
# RESULT: macro foo
# RESULT: macro foo in C
# RESULT: macro fooBar
# RESULT: macro fooBar in C

macro foo
end

macro bar
end

macro fooBar
end

class C
  macro foo
  end

  macro bar
  end

  macro fooBar
  end
end