# KIND: SUBTYPES

module M

end

struct A

end

struct B < A
  include M
end

module N
  include M
end

struct <caret>C < B
  include N
end