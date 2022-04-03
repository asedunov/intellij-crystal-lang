# INDEX: CrystalTypeFqNameIndex
# KEY: A::B::C

annotation A
end

annotation ::A
end

annotation B::A
end

annotation A::B
end

<caret>annotation A::B::C
end

<caret>annotation ::A::B::C
end


module M
  annotation A::B::C
  end

  <caret>annotation ::A::B::C
  end
end

module A
  <caret>annotation B::C
  end

  annotation ::B::C
  end

  <caret>annotation ::A::B::C
  end

  module B
    <caret>annotation C
    end

    annotation ::C
    end

    <caret>annotation ::A::B::C
    end
  end
end

module A::B
  <caret>annotation C
  end

  annotation ::C
  end

  <caret>annotation ::A::B::C
  end
end