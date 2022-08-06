# SUPER_CLASS: CA
# PARENTS: MC, MA, CA

module MA

end

module MB

end

module MC

end

module MD

end

class CA

end

class <caret>CB < CA
  include MA
  extend MB
  include MC
  extend MD
end