annotation A
end

@[Foo]
annotation A
end

annotation X::Y::A
end

annotation ::X::Y::A
end

module M
    annotation A
    end

    annotation X::Y::A
    end

    annotation ::X::Y::A
    end
end