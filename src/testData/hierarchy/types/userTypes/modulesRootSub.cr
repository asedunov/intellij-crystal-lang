# KIND: SUBTYPES

module <caret>M

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

module P
  include N
end