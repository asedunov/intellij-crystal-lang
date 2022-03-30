# INDEX: CrystalTypeByNamespaceFqNameIndex
# KEY: M::N

alias A = Int32
alias ::B = Int32
alias M::C = Int32
alias ::M::D = Int32
<caret>alias M::N::C = Int32
<caret>alias ::M::N::D = Int32

module M
  alias A = Int32
  alias ::B = Int32
  alias C::D = Int32
  alias ::E::F = Int32
  <caret>alias N::D = Int32
  alias ::N::F = Int32
  <caret>alias ::M::N::G = Int32

  module N
    <caret>alias A = Int32
    alias ::B = Int32
    alias C::D = Int32
    alias ::E::F = Int32
    <caret>alias ::M::N::G = Int32
  end
end

module M::N
  <caret>alias A = Int32
  alias ::B = Int32
  alias C::D = Int32
  alias ::E::F = Int32
  <caret>alias ::M::N::G = Int32
end