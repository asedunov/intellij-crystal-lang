annotation Foo
end

class Bar
end

@[Foo]
@[<error descr="Annotation is expected, but class is found">Bar</error>]
class C
end