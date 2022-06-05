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

extend <error descr="'include'/'extend' target must be a module">C</error>
extend <error descr="'include'/'extend' target must be a module">S</error>
extend M
extend <error descr="'include'/'extend' target must be a module">E</error>
extend <error descr="'include'/'extend' target must be a module">Ann</error>
extend <error descr="'include'/'extend' target must be a module">L</error>
extend <error descr="'include'/'extend' target must be a module">L::S</error>
extend <error descr="'include'/'extend' target must be a module">L::U</error>
extend <error descr="'include'/'extend' target must be a module">L::T</error>
extend <error descr="'include'/'extend' target must be a module">A</error>