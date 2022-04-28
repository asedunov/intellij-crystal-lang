include A

include A::B

include A(T)

include self

class X
  include A

  include A::B

  include A(T)

  include self
end

module X
  include A

  include A::B

  include A(T)

  include self
end