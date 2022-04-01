# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

annotation A
end

annotation ::B
end

annotation M::C
end

annotation ::M::D
end

<caret>annotation M::N::C
end

<caret>annotation ::M::N::D
end

module M
  annotation A
  end

  annotation ::B
  end

  annotation C::D
  end

  annotation ::E::F
  end

  <caret>annotation N::D
  end

  annotation ::N::F
  end

  <caret>annotation ::M::N::G
  end

  module N
    <caret>annotation A
    end

    annotation ::B
    end

    annotation <caret>C::D
    end

    annotation ::E::F
    end

    <caret>annotation ::M::N::G
    end
  end
end

module M::N
  <caret>annotation A
  end

  annotation ::B
  end

  annotation <caret>C::D
  end

  annotation ::E::F
  end

  <caret>annotation ::M::N::G
  end
end