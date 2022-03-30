# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY:

<caret>module A
end

<caret>module ::B
end

module C::D
end

module ::E::F
end

<caret>module M
  module A
  end

  <caret>module ::B
  end

  module C::D
  end

  module ::E::F
  end
end