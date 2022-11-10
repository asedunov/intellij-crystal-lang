# INDEX: CrystalConstantFqNameIndex
# KEY: A::B

A = Int32
::A = Int32
B::A = Int32
<caret>A::B = Int32
<caret>::A::B = Int32
<caret>A::B::C = Int32

module M
  A::B = Int32
  <caret>::A::B = Int32
end

module A
  <caret>B = Int32
  ::B = Int32
  <caret>::A::B = Int32
end