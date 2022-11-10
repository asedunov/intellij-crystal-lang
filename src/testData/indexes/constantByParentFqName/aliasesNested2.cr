# INDEX: CrystalConstantParentFqNameIndex
# KEY: A::B

alias A = Int32
alias ::A = Int32
alias B::A = Int32
alias A::B = Int32
<caret>alias A::B::C = Int32
<caret>alias ::A::B::C = Int32

module M
  alias A::B::C = Int32
  <caret>alias ::A::B::C = Int32
end

module A
  <caret>alias B::C = Int32
  alias ::B::C = Int32
  <caret>alias ::A::B::C = Int32
  module B
    <caret>alias C = Int32
    alias ::C = Int32
    <caret>alias ::A::B::C = Int32
  end
end

module A::B
  <caret>alias C = Int32
  alias ::C = Int32
  <caret>alias ::A::B::C = Int32
end