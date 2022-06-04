private<EOLError descr="Expected: <expression>"></EOLError>

protected<EOLError descr="Expected: <expression>"></EOLError>

<error descr="Visibility modifier is not supported here">private</error> 1 + 2

<error descr="Visibility modifier is not supported here">protected</error> 1 + 2

private foo 1, 2

protected foo 1, 2

private X1 = 1

<error descr="Can only use 'private' for constants">protected</error> X2 = 1

<error descr="Visibility modifier is not supported here">private</error> x1 = 1

<error descr="Visibility modifier is not supported here">protected</error> x2 = 1

private def foo1
end

protected def foo2
end

<error descr="Visibility modifier is not supported here">private</error> fun foo3
end

<error descr="Visibility modifier is not supported here">protected</error> fun foo4
end

private class C1
end

<error descr="Can only use 'private' for types">protected</error> class C2
end

private struct S1
end

<error descr="Can only use 'private' for types">protected</error> struct S2
end

private module M1
end

<error descr="Can only use 'private' for types">protected</error> module M2
end

private enum E1
  X
end

<error descr="Can only use 'private' for types">protected</error> enum E2
  X
end

private alias A1 = Int32

<error descr="Can only use 'private' for types">protected</error> alias A2 = Int32

private lib L1
end

<error descr="Can only use 'private' for types">protected</error> lib L2
end

private macro mmm1
end

<error descr="Can only use 'private' for macros">protected</error> macro mmm2
end