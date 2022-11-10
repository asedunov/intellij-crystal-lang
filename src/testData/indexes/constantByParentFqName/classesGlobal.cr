# INDEX: CrystalConstantParentFqNameIndex
# KEY:

<caret>class A
end

<caret>class ::A
end

class <caret>B::A
end

class <caret>A::B
end

<caret>module M
  class A
  end

  <caret>class ::A
  end
end