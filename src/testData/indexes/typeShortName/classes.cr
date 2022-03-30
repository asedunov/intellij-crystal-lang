# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>class A
end

class B
end

<caret>class ::A
end

class ::B
end

class A::X
end

<caret>class X::A
end

module M
  <caret>class A
  end

  class B
  end

  <caret>class ::A
  end

  class ::B
  end

  class A::X
  end

  <caret>class X::A
  end
end