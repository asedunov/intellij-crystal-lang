module A

end

module A<error descr="A is not a generic module">(T, U)</error>

end

module B(T, U)

end

module B

end

module C(T, U, V)

end

module C<error descr="Type parameters must be T, U, V, not X, Y, Z">(X, Y, Z)</error>

end

module D(T, *U, V)

end

module D<error descr="Type parameters must be T, *U, V, not T, U, V">(T, U, V)</error>

end