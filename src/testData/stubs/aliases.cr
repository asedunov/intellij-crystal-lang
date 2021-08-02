alias A = Int32

private alias A = Int32

@[Foo]
alias A = Int32

alias X::Y::A = Int32

alias ::X::Y::A = Int32

module M
    alias A = Int32

    alias X::Y::A = Int32

    alias ::X::Y::A = Int32
end