class C

end

struct S

end

module M

end

enum E
  X
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

class C::X

end

class S::X

end

class M::X

end

class <error descr="Can't declare type inside enum \"e\"">E</error>::X

end

class <error descr="Can't declare type inside annotation \"ann\"">Ann</error>::X

end

class <error descr="Can't declare type inside library \"l\"">L</error>::X

end

class L::<error descr="Can't declare type inside struct \"s\"">S</error>::X

end

class L::<error descr="Can't declare type inside union \"u\"">U</error>::X

end

class L::<error descr="Can't declare type inside type declaration \"t\"">T</error>::X

end

class <error descr="Can't declare type inside alias \"a\"">A</error>::X

end