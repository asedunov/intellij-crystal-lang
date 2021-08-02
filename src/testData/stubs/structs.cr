struct A
end

private struct A
end

abstract struct A
end

@[Foo]
struct A
end

struct X::Y::A
end

struct ::X::Y::A
end

module M
    struct A
    end

    struct X::Y::A
    end

    struct ::X::Y::A
    end
end