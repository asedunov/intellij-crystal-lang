# SUPER_CLASS: C
# PARENTS: B, A, C

module A

end

module B

end

class C

end

class <caret>D < C
  include A
  include B
end