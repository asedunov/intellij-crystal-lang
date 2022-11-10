# INDEX: CrystalConstantParentFqNameIndex
# KEY: A

module A
end

module ::A
end

module B::A
end  

<caret>module A::B
end

<caret>module ::A::B
end

module <caret>A::B::C
end

module M
  module A::B
  end

  <caret>module ::A::B
  end
end

module A
  <caret>module B
  end

  module ::B
  end

  <caret>module ::A::B
  end
end