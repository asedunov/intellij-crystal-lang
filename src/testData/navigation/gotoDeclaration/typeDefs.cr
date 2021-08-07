# SEARCH_TEXT: foo
# RESULT: type definition Foo in L
# RESULT: type definition FooBar in L

lib L
  type Foo = Int32
  type FooBar = Int32
  type Bar = Int32
end