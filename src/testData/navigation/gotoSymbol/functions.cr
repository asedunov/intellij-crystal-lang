# SEARCH_TEXT: foo
# RESULT: function foo
# RESULT: function foo in L
# RESULT: function fooBar
# RESULT: function fooBar in L

fun foo
end

fun bar
end

fun fooBar
end

lib L
  fun foo

  fun bar

  fun fooBar
end