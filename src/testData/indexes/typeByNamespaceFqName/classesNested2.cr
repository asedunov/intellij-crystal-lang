# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

class A
end

class ::B
end

class M::C
end

class ::M::D
end

<caret>class M::N::C
end

<caret>class ::M::N::D
end

module M
  class A
  end

  class ::B
  end

  class C::D
  end

  class ::E::F
  end

  <caret>class N::D
  end

  class ::N::F
  end

  <caret>class ::M::N::G
  end

  module N
    <caret>class A
    end

    class ::B
    end

    class C::D
    end

    class ::E::F
    end

    <caret>class ::M::N::G
    end
  end
end

module M::N
  <caret>class A
  end

  class ::B
  end

  class C::D
  end

  class ::E::F
  end

  <caret>class ::M::N::G
  end
end