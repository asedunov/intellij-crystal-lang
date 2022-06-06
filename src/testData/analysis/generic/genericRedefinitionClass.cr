class A

end

class A<error descr="A is not a generic class">(T, U)</error>

end

class B(T, U)

end

class B

end

class C(T, U, V)

end

class C<error descr="Type parameters must be T, U, V, not X, Y, Z">(X, Y, Z)</error>

end

class D(T, *U, V)

end

class D<error descr="Type parameters must be T, *U, V, not T, U, V">(T, U, V)</error>

end