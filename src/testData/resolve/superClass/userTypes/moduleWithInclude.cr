# SUPER_CLASS:
# PARENTS: B, A

module A

end

module B

end

module <caret>C
  include A
  include B
end