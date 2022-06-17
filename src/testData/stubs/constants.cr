A = 1

private A = 1

@[Foo]
A = 1

X::Y::A = 1

::X::Y::A = 1

module M
    A = 1

    X::Y::A = 1

    ::X::Y::A = 1
end