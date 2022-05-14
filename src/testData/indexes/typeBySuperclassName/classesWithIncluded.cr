# INDEX: CrystalTypeBySuperclassNameIndex
# KEY: M

module M
end

module N
end

<caret>class B
  include M
end

<caret>class C
  include M
end

class D
  include N
end

<caret>class E
  include X::M
end

class F
  include M::X
end

<caret>class G
  include M(X, Y)
end

class H
  include X(M, N)
end