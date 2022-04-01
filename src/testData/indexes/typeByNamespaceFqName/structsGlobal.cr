# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY:

<caret>struct A
end

<caret>struct ::B
end

struct <caret>C::D
end

struct <caret>::E::F
end

<caret>module M
  struct A
  end

  <caret>struct ::B
  end

  struct C::D
  end

  struct <caret>::E::F
  end
end