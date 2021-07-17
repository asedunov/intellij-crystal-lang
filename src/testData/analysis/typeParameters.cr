class A1(T, U, V)
end

class A2(T, <error descr="Duplicated type parameter name: U">U</error>, V, <error descr="Duplicated type parameter name: U">U</error>)
end

class A3(*T, U, V)
end

class A4(*T, U, <error descr="Splat type parameter already specified">*V</error>)
end

module M1(T, U, V)
end

module M2(T, <error descr="Duplicated type parameter name: U">U</error>, V, <error descr="Duplicated type parameter name: U">U</error>)
end

module M3(*T, U, V)
end

module M4(*T, U, <error descr="Splat type parameter already specified">*V</error>)
end

struct S1(T, U, V)
end

struct S2(T, <error descr="Duplicated type parameter name: U">U</error>, V, <error descr="Duplicated type parameter name: U">U</error>)
end

struct S3(*T, U, V)
end

struct S4(*T, U, <error descr="Splat type parameter already specified">*V</error>)
end

def foo1(x : T, y : U) forall T, U
end

def foo1(x : T, y : U) forall T, U, T
end