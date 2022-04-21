# KIND: SUPERTYPES

module M

end

class <caret>A

end

class B < A
  include M
end

module N
  include M
end

class C < B
  include N
end