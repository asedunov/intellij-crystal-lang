# INDEX: CrystalConstantParentFqNameIndex
# KEY:

<caret>module A
end

<caret>module ::A
end

module <caret>B::A
end

module <caret>A::B
end

<caret>module M
  module A
  end

  <caret>module ::A
  end
end