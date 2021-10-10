# SEARCH_TEXT: foo
# RESULT: class variable foo in X
# RESULT: class variable fooBar in X
# RESULT: instance variable foo in X
# RESULT: instance variable fooBar in X
# RESULT: variable foo
# RESULT: variable fooBar

foo : Int8 = 1
bar : Int8 = 1
fooBar : Int8 = 1

def fff
    foo : Int8 = 1
    bar : Int8 = 1
    fooBar : Int8 = 1
end

class X
    foo : Int8 = 1
    bar : Int8 = 1
    fooBar : Int8 = 1

    @foo : Int8 = 1
    @bar : Int8 = 1
    @fooBar : Int8 = 1

    @@foo : Int8 = 1
    @@bar : Int8 = 1
    @@fooBar : Int8 = 1
end