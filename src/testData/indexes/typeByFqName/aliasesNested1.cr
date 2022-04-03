# INDEX: CrystalTypeFqNameIndex
# KEY: A::B

alias A = Int32
alias ::A = Int32
alias B::A = Int32
<caret>alias A::B = Int32
<caret>alias ::A::B = Int32
alias <caret>A::B::C = Int32

module M
  alias A::B = Int32
  <caret>alias ::A::B = Int32
end

module A
  <caret>alias B = Int32
  alias ::B = Int32
  <caret>alias ::A::B = Int32
end