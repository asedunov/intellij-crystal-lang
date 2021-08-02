module A
end

private module A
end

@[Foo]
module A
end

module X::Y::A
end

module ::X::Y::A
end

module M
    module A
    end

    module X::Y::A
    end

    module ::X::Y::A
    end
end