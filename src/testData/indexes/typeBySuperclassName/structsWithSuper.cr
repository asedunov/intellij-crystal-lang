# INDEX: CrystalTypeBySuperclassNameIndex
# KEY: A

struct A
end

<caret>struct B < A
end

<caret>struct C < A
end

struct D < B
end

<caret>struct E < X::A
end

struct F < A::X
end

<caret>struct G < A(X, Y)
end

struct H < X(A, B)
end