require "aaa"

require "aaa<error descr="Interpolation is not allowed in 'require' expression">#{123}</error>"

require "foo"\
"bar"

require "foo<error descr="Interpolation is not allowed in 'require' expression">#{123}</error>"\
"bar<error descr="Interpolation is not allowed in 'require' expression">#{456}</error>"

fun foo = "abc"
end

fun foo = "abc<error descr="Interpolation is not allowed in function name">#{123}</error>def"
end

fun foo = "abc"\
          "def"
end

fun foo = "abc<error descr="Interpolation is not allowed in function name">#{123}</error>"\
          "def<error descr="Interpolation is not allowed in function name">#{456}</error>"
end

def foo("abc" x : Int32)
end

def foo("abc<error descr="Interpolation is not allowed in external name">#{123}</error>" x : Int32)
end

def foo("abc"\
        "def" x : Int32)
end

def foo("abc<error descr="Interpolation is not allowed in external name">#{123}</error>"\
        "def<error descr="Interpolation is not allowed in external name">#{456}</error>" x : Int32)
end

foo("abc": 1, "def": 2)

foo("abc": 1, "def<error descr="Interpolation is not allowed in named argument">#{456}</error>": 2)
foo("abc<error descr="Interpolation is not allowed in named argument">#{123}</error>": 1, "def": 2)

foo("abc"\
    "def": 1)

foo("abc<error descr="Interpolation is not allowed in named argument">#{123}</error>"\
    "def<error descr="Interpolation is not allowed in named argument">#{456}</error>": 1)

x = {"abc": 1, "def": 2}

x = {"abc": 1, "def<error descr="Interpolation is not allowed in named tuple name">#{456}</error>": 2}
x = {"abc<error descr="Interpolation is not allowed in named tuple name">#{123}</error>": 1, "def": 2}

x = {"abc"\
    "def": 1}

x = {"abc<error descr="Interpolation is not allowed in named tuple name">#{123}</error>"\
    "def<error descr="Interpolation is not allowed in named tuple name">#{456}</error>": 1}

def foo(x : {"abc": Int32, "def": String})
end

def foo(x : {"abc": Int32, "def<error descr="Interpolation is not allowed in named argument">#{456}</error>)": String})
end

def foo(x : {"abc<error descr="Interpolation is not allowed in named argument">#{123}</error>)": Int32, "def": String})
end

def foo(x : {"abc"\
             "def": Int32})
end

def foo(x : {"abc<error descr="Interpolation is not allowed in named argument">#{123}</error>)"\
             "def<error descr="Interpolation is not allowed in named argument">#{456}</error>)": Int32})
end

def foo(x : Foo("abc": Int32, "def": String))
end

def foo(x : Foo("abc": Int32, "def<error descr="Interpolation is not allowed in named argument">#{456}</error>)": String))
end

def foo(x : Foo("abc<error descr="Interpolation is not allowed in named argument">#{123}</error>)": Int32, "def": String))
end

def foo(x : Foo("abc"\
                "def": Int32))
end

def foo(x : Foo("abc<error descr="Interpolation is not allowed in named argument">#{123}</error>)"\
                "def<error descr="Interpolation is not allowed in named argument">#{456}</error>)": Int32))
end