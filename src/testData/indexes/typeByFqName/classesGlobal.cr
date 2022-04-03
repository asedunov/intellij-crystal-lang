# INDEX: CrystalTypeFqNameIndex
# KEY: A

<caret>class A
end

<caret>class ::A
end

class B::A
end

class <caret>A::B
end

module M
  class A
  end

  <caret>class ::A
  end
end