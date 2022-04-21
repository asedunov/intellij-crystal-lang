# KIND: SUPERTYPES

module M

end

class A
  include M
end

module <caret>N
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

module P
  include N
end