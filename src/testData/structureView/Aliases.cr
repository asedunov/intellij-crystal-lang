alias Foo = Bar

alias Foo2 = A::B::C

alias Foo3 = ::A::B::C

alias Mod1::Foo = Mod2::Bar

module MyModule
  alias Foo = Bar

  alias Mod1::Foo = Mod2::Bar
end

class MyClass
  alias Foo = Bar

  alias Mod1::Foo = Mod2::Bar
end

lib MyLib
  alias Foo = Bar

  alias Mod1::Foo = Mod2::Bar
end