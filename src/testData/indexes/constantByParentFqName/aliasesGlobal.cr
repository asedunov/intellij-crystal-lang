# INDEX: CrystalConstantParentFqNameIndex
# KEY:

<caret>alias A = Int32
<caret>alias ::A = Int32
alias <caret>B::A = Int32
alias <caret>A::B = Int32

<caret>module M
  alias A = Int32
  <caret>alias ::A = Int32
end