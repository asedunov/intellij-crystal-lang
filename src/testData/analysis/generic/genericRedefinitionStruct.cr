struct A

end

struct A<error descr="A is not a generic struct">(T, U)</error>

end

struct  B(T, U)

end

struct  B

end

struct  C(T, U, V)

end

struct  C<error descr="Type parameters must be T, U, V, not X, Y, Z">(X, Y, Z)</error>

end

struct  D(T, *U, V)

end

struct  D<error descr="Type parameters must be T, *U, V, not T, U, V">(T, U, V)</error>

end