# LANGUAGE_LEVEL: 1.5

def foo1(x : T, y : U) forall T, U
end

def foo1(x : T, y : U) forall <error descr="Duplicated type parameter name: T">T</error>, U, <error descr="Duplicated type parameter name: T">T</error>
end