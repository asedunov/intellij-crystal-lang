# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY:

<caret>alias A = Int32
<caret>alias ::B = Int32
alias C::D = Int32
alias ::E::F = Int32

<caret>module M
  alias A = Int32
  <caret>alias ::B = Int32
  alias C::D = Int32
  alias ::E::F = Int32
end