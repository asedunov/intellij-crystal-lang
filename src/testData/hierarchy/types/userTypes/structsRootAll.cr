# KIND: TYPES

module M

end

struct <caret>A

end

struct B < A
  include M
end

module N
  include M
end

struct C < B
  include N
end