# KIND: SUPERTYPES

module M

end

class A

end

class <caret>B < A
  include M
end

module N
  include M
end

class C < B
  include N
end