class C

end

struct S

end

module M

end

enum E

end

annotation Ann
end

lib L
  struct S

  end

  union U

  end

  type T = Int32
end

alias A = Int32

include <error descr="'include'/'extend' target must be a module">C</error>
include <error descr="'include'/'extend' target must be a module">S</error>
include M
include <error descr="'include'/'extend' target must be a module">E</error>
include <error descr="'include'/'extend' target must be a module">Ann</error>
include <error descr="'include'/'extend' target must be a module">L</error>
include <error descr="'include'/'extend' target must be a module">L::S</error>
include <error descr="'include'/'extend' target must be a module">L::U</error>
include <error descr="'include'/'extend' target must be a module">L::T</error>
include <error descr="'include'/'extend' target must be a module">A</error>