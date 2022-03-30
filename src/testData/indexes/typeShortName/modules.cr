# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>module A
end

module B
end

<caret>module ::A
end

module ::B
end

module A::X
end

<caret>module X::A
end

module M
  <caret>module A
  end

  module B
  end

  <caret>module ::A
  end

  module ::B
  end

  module A::X
  end

  <caret>module X::A
  end
end