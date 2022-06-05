module A(T, U)

end

class B
  include <error descr="Generic type arguments must be specified when inheriting A(T, U)">A</error>
  extend <error descr="Generic type arguments must be specified when inheriting A(T, U)">A</error>
end

class C
  include A(Int32, String)
  extend A(Int32, String)
end