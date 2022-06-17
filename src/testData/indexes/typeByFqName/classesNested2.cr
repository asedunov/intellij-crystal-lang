# INDEX: CrystalConstantFqNameIndex
# KEY: A::B::C

class A
end

class ::A
end

class B::A
end

class A::B
end

<caret>class A::B::C
end

<caret>class ::A::B::C
end


module M
  class A::B::C
  end

  <caret>class ::A::B::C
  end
end

module A
  <caret>class B::C
  end

  class ::B::C
  end

  <caret>class ::A::B::C
  end

  module B
    <caret>class C
    end

    class ::C
    end

    <caret>class ::A::B::C
    end
  end
end

module A::B
  <caret>class C
  end

  class ::C
  end

  <caret>class ::A::B::C
  end
end