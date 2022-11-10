# INDEX: CrystalConstantFqNameIndex
# KEY: A

<caret>module A
end

<caret>module ::A
end

module B::A
end

module <caret>A::B
end

module M
  module A
  end

  <caret>module ::A
  end
end