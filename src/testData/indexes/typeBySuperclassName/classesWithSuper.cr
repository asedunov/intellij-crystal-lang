# INDEX: CrystalTypeBySuperclassNameIndex
# KEY: A

class A
end

<caret>class B < A
end

<caret>class C < A
end

class D < B
end

<caret>class E < X::A
end

class F < A::X
end

<caret>class G < A(X, Y)
end

class H < X(A, B)
end