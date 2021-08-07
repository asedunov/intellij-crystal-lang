# SEARCH_TEXT: foo
# RESULT: alias Foo
# RESULT: alias Foo in X
# RESULT: alias FooBar
# RESULT: alias FooBar in X

alias Foo = Int32
alias FooBar = Int32
alias Bar = Int32

module X
  alias Foo = Int32
  alias FooBar = Int32
  alias Bar = Int32
end