# KIND: TYPES

module M

end

class A

end

class B < A
  include M
end

module N
  include M
end

class <caret>C < B
  include N
end