# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY:

<caret>struct A
end

<caret>struct ::B
end

struct C::D
end

struct ::E::F
end

<caret>module M
  struct A
  end

  <caret>struct ::B
  end

  struct C::D
  end

  struct ::E::F
  end
end