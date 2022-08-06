# META_SUPER_CLASS: Class
# META_PARENTS: D, B, Class

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