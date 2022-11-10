# INDEX: CrystalConstantParentFqNameIndex
# KEY: A::B

struct A
end

struct ::A
end

struct B::A
end

struct A::B
end

<caret>struct A::B::C
end

<caret>struct ::A::B::C
end


module M
  struct A::B::C
  end

  <caret>struct ::A::B::C
  end
end

module A
  <caret>struct B::C
  end

  struct ::B::C
  end

  <caret>struct ::A::B::C
  end

  module B
    <caret>struct C
    end

    struct ::C
    end

    <caret>struct ::A::B::C
    end
  end
end

module A::B
  <caret>struct C
  end

  struct ::C
  end

  <caret>struct ::A::B::C
  end
end