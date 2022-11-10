# INDEX: CrystalConstantParentFqNameIndex
# KEY: A::B

A = Int32
::A = Int32
B::A = Int32
A::B = Int32
<caret>A::B::C = Int32
<caret>::A::B::C = Int32

module M
  A::B::C = Int32
  <caret>::A::B::C = Int32
end

module A
  <caret>B::C = Int32
  ::B::C = Int32
  <caret>::A::B::C = Int32
  module B
    <caret>C = Int32
    ::C = Int32
    <caret>::A::B::C = Int32
  end
end

module A::B
  <caret>C = Int32
  ::C = Int32
  <caret>::A::B::C = Int32
end