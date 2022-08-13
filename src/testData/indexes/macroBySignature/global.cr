# INDEX: CrystalMacroSignatureIndex
# KEY: foo(0|-1|0|)

<caret>macro foo
end

macro foo(a)
end

macro bar
end

class A
  <caret>macro foo
  end

  macro foo(a)
  end

  macro bar
  end
end