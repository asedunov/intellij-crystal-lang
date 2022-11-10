# INDEX: CrystalConstantFqNameIndex
# KEY: A

<caret>alias A = Int32
<caret>alias ::A = Int32
alias B::A = Int32
alias <caret>A::B = Int32

module M
  alias A = Int32
  <caret>alias ::A = Int32
end