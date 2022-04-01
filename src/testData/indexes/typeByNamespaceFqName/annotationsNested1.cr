# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

annotation A
end

annotation ::B
end

<caret>annotation M::C
end

<caret>annotation ::M::D
end

module M
  <caret>annotation A
  end

  annotation ::B
  end

  annotation <caret>C::D
  end

  annotation ::E::F
  end

  <caret>annotation ::M::G
  end
end