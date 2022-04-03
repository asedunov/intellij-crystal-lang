# INDEX: CrystalTypeFqNameIndex
# KEY: A::B

class A
end

class ::A
end

class B::A
end  

<caret>class A::B
end

<caret>class ::A::B
end

class <caret>A::B::C
end

module M
  class A::B
  end

  <caret>class ::A::B
  end
end

module A
  <caret>class B
  end

  class ::B
  end

  <caret>class ::A::B
  end
end