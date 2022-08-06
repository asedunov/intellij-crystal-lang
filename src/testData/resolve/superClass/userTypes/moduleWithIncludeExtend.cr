# SUPER_CLASS:
# PARENTS: C, A

module A

end

module B

end

module C

end

module D

end

module <caret>M
  include A
  extend B
  include C
  extend D
end