# INDEX: CrystalMacroFqNameIndex
# KEY: A.foo

macro foo
end

macro bar
end

class A
  <caret>macro foo
  end

  macro bar
  end
end

class B
  macro foo
  end

  macro bar
  end
end