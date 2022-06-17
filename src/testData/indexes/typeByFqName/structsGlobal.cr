# INDEX: CrystalConstantFqNameIndex
# KEY: A

<caret>struct A
end

<caret>struct ::A
end

struct B::A
end

struct <caret>A::B
end

module M
  struct A
  end

  <caret>struct ::A
  end
end