# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M

alias A = Int32
alias ::B = Int32
<caret>alias M::C = Int32
<caret>alias ::M::D = Int32

module M
  <caret>alias A = Int32
  alias ::B = Int32
  alias <caret>C::D = Int32
  alias ::E::F = Int32
  <caret>alias ::M::G = Int32
end