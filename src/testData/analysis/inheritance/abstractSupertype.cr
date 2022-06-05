class A

end

class SubA < A

end

abstract class AbstractA

end

class SubAbstractA < AbstractA

end

struct S

end

struct <error descr="Can't inherit from a non-abstract struct">SubS</error> < S

end

abstract struct AbstractS

end

struct SubAbstractS < AbstractS

end

struct <error descr="Can't inherit from a non-abstract struct">MyInt32</error> < Int32

end

struct MyStruct < Struct

end