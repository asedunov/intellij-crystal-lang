# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

struct A
end

struct ::B
end

<caret>struct M::C
end

<caret>struct ::M::D
end

module M
  <caret>struct A
  end

  struct ::B
  end

  struct <caret>C::D
  end

  struct ::E::F
  end

  <caret>struct ::M::G
  end
end