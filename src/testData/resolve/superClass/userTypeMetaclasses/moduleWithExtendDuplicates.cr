# META_SUPER_CLASS: Class
# META_PARENTS: B, A, Class

module A

end

module B

end

module <caret>C
  extend A
  extend B
  extend A
end