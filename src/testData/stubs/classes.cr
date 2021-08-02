class A
end

private class A
end

abstract class A
end

@[Foo]
class A
end

class X::Y::A
end

class ::X::Y::A
end

module M
    class A
    end

    class X::Y::A
    end

    class ::X::Y::A
    end
end