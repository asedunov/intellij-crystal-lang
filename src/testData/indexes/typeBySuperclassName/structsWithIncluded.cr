# INDEX: CrystalTypeBySuperclassNameIndex
# KEY: M

module M
end

module N
end

<caret>struct B
  include M
end

<caret>struct C
  include M
end

struct D
  include N
end

<caret>struct E
  include X::M
end

struct F
  include M::X
end

<caret>struct G
  include M(X, Y)
end

struct H
  include X(M, N)
end