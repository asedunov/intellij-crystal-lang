# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

module A
end

module ::B
end

<caret>module M::C
end

<caret>module ::M::D
end

module M
  <caret>module A
  end

  module ::B
  end

  module C::D
  end

  module ::E::F
  end

  <caret>module ::M::G
  end
end