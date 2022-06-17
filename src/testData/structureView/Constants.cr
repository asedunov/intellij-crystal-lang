Foo = Bar

Foo2 = A::B::C

Foo3 = ::A::B::C

Mod1::Foo = Mod2::Bar

module MyModule
  Foo = Bar

  Mod1::Foo = Mod2::Bar
end

class MyClass
  Foo = Bar

  Mod1::Foo = Mod2::Bar
end

lib MyLib
  Foo = Bar

  Mod1::Foo = Mod2::Bar
end