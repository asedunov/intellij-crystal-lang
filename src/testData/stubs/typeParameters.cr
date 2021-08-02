class A(T, U)
end

struct A(T, U)
end

module A(T, U)
end

def foo(t : T, u : U) forall T, U
end