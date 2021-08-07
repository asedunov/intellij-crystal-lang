# SEARCH_TEXT: foo
# RESULT: field foo in L::S
# RESULT: field foo in L::U
# RESULT: field fooBar in L::S
# RESULT: field fooBar in L::U

lib L
  struct S
    foo : Int32
    bar : Int32
    fooBar : Int32
  end

  struct U
    foo : Int32
    bar : Int32
    fooBar : Int32
  end
end