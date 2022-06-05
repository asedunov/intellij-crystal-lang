class C

end

struct S

end

class CC < C

end

struct <error descr="Can't inherit from a non-abstract struct">SS</error> < S

end

class CCC < <error descr="Can't inherit class from struct">S</error>

end

struct SSS < <error descr="Can't inherit struct from class">C</error>

end