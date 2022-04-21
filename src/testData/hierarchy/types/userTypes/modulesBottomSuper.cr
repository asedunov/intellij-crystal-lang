# KIND: SUPERTYPES

module M

end

class A
  include M
end

module N
  include M
end

class B < A
  include N
end

struct AA
  include M
end

class BB < AA
  include N
end

module <caret>P
  include N
end