# SEARCH_TEXT: FOO
# RESULT: class variable FOO in X
# RESULT: class variable FOO_BAR in X
# RESULT: constant FOO
# RESULT: constant FOO in X
# RESULT: constant FOO_BAR
# RESULT: constant FOO_BAR in X
# RESULT: instance variable FOO in X
# RESULT: instance variable FOO_BAR in X

FOO = 1
BAR = 1
FOO_BAR = 1

class X
    FOO = 1
    BAR = 1
    FOO_BAR = 1

    @FOO : Int8 = 1
    @BAR : Int8 = 1
    @FOO_BAR : Int8 = 1

    @@FOO : Int8 = 1
    @@BAR : Int8 = 1
    @@FOO_BAR : Int8 = 1
end