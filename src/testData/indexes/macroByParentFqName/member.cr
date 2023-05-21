# INDEX: CrystalMacroParentFqNameIndex
# KEY: A

macro foo
end

macro bar
end

class A
  <caret>macro foo
  end

  <caret>macro bar
  end
end

class B
  macro foo
  end

  macro bar
  end
end