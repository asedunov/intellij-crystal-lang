# INDEX: CrystalTypeBySuperclassNameIndex
# KEY: M

module M
end

module N
end

<caret>module B
  include M
end

<caret>module C
  include M
end

module D
  include N
end

<caret>module E
  include X::M
end

module F
  include M::X
end

<caret>module G
  include M(X, Y)
end

module H
  include X(M, N)
end