# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>alias A = Int32

alias B = Int32

<caret>alias ::A = Int32

alias ::B = Int32

alias A::X = Int32

<caret>alias X::A = Int32

module M
  <caret>alias A = Int32

  alias B = Int32

  <caret>alias ::A = Int32

  alias ::B = Int32

  alias A::X = Int32

  <caret>alias X::A = Int32
end