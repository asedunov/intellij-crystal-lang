def foo(@[Foo] var); end
def foo(@[Foo] outer inner); end
def foo(@[Foo]  var); end
def foo(a, @[Foo] var); end
def foo(a, @[Foo] &block); end
def foo(@[Foo] @var); end
def foo(@[Foo] var : Int32); end
def foo(@[Foo] @[Bar] var : Int32); end
def foo(@[Foo] &@block); end
def foo(@[Foo] *args); end
def foo(@[Foo] **args); end
def foo(
  @[Foo]
  id : Int32,
  @[Bar] name : String
); end

macro foo(@[Foo] var);end
macro foo(@[Foo] outer inner);end
macro foo(@[Foo]  var);end
macro foo(a, @[Foo] var);end
macro foo(a, @[Foo] &block);end
macro foo(@[Foo] *args);end
macro foo(@[Foo] **args);end
macro foo(
  @[Foo]
  id,
  @[Bar] name
);end