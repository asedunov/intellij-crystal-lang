# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

struct A
end

struct ::B
end

struct M::C
end

struct ::M::D
end

<caret>struct M::N::C
end

<caret>struct ::M::N::D
end

module M
  struct A
  end

  struct ::B
  end

  struct C::D
  end

  struct ::E::F
  end

  <caret>struct N::D
  end

  struct ::N::F
  end

  <caret>struct ::M::N::G
  end

  module N
    <caret>struct A
    end

    struct ::B
    end

    struct C::D
    end

    struct ::E::F
    end

    <caret>struct ::M::N::G
    end
  end
end

module M::N
  <caret>struct A
  end

  struct ::B
  end

  struct C::D
  end

  struct ::E::F
  end

  <caret>struct ::M::N::G
  end
end