# INDEX: CrystalTypeFqNameIndex
# KEY: A::B

annotation A
end

annotation ::A
end

annotation B::A
end  

<caret>annotation A::B
end

<caret>annotation ::A::B
end

annotation <caret>A::B::C
end

module M
  annotation A::B
  end

  <caret>annotation ::A::B
  end
end

module A
  <caret>annotation B
  end

  annotation ::B
  end

  <caret>annotation ::A::B
  end
end