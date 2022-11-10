# INDEX: CrystalConstantFqNameIndex
# KEY: A::B::C

module A
end

module ::A
end

module B::A
end

module A::B
end

<caret>module A::B::C
end

<caret>module ::A::B::C
end


module M
  module A::B::C
  end

  <caret>module ::A::B::C
  end
end

module A
  <caret>module B::C
  end

  module ::B::C
  end

  <caret>module ::A::B::C
  end

  module B
    <caret>module C
    end

    module ::C
    end

    <caret>module ::A::B::C
    end
  end
end

module A::B
  <caret>module C
  end

  module ::C
  end

  <caret>module ::A::B::C
  end
end