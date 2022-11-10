# INDEX: CrystalConstantParentFqNameIndex
# KEY: A

struct A
end

struct ::A
end

struct B::A
end  

<caret>struct A::B
end

<caret>struct ::A::B
end

struct <caret>A::B::C
end

module M
  struct A::B
  end

  <caret>struct ::A::B
  end
end

module A
  <caret>struct B
  end

  struct ::B
  end

  <caret>struct ::A::B
  end
end