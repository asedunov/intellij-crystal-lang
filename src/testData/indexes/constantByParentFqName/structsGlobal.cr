# INDEX: CrystalConstantParentFqNameIndex
# KEY:

<caret>struct A
end

<caret>struct ::A
end

struct <caret>B::A
end

struct <caret>A::B
end

<caret>module M
  struct A
  end

  <caret>struct ::A
  end
end