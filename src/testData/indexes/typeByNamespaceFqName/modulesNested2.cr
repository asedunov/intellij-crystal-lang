# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

module A
end

module ::B
end

module M::C
end

module ::M::D
end

<caret>module M::N::C
end

<caret>module ::M::N::D
end

module M
  module A
  end

  module ::B
  end

  module C::D
  end

  module ::E::F
  end

  <caret>module N::D
  end

  module ::N::F
  end

  <caret>module ::M::N::G
  end

  module N
    <caret>module A
    end

    module ::B
    end

    module C::D
    end

    module ::E::F
    end

    <caret>module ::M::N::G
    end
  end
end

module M::N
  <caret>module A
  end

  module ::B
  end

  module C::D
  end

  module ::E::F
  end

  <caret>module ::M::N::G
  end
end