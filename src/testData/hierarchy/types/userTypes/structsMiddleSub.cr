# KIND: SUBTYPES

module M

end

struct A

end

struct <caret>B < A
  include M
end

module N
  include M
end

struct C < B
  include N
end