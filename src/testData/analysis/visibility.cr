private<EOLError descr="Expected: <expression>"></EOLError>

protected<EOLError descr="Expected: <expression>"></EOLError>

<error descr="Visibility modifier is not supported here">private</error> 1 + 2

<error descr="Visibility modifier is not supported here">protected</error> 1 + 2

private foo 1, 2

protected foo 1, 2

private A = 1

<error descr="Can only use 'private' for constants">protected</error> A = 1

<error descr="Visibility modifier is not supported here">private</error> a = 1

<error descr="Visibility modifier is not supported here">protected</error> a = 1

private def foo
end

protected def foo
end

<error descr="Visibility modifier is not supported here">private</error> fun foo
end

<error descr="Visibility modifier is not supported here">protected</error> fun foo
end

private class A
end

<error descr="Can only use 'private' for types">protected</error> class A
end

private struct A
end

<error descr="Can only use 'private' for types">protected</error> struct A
end

private module A
end

<error descr="Can only use 'private' for types">protected</error> module A
end

private enum A
  X
end

<error descr="Can only use 'private' for types">protected</error> enum A
  X
end

private alias A = Int32

<error descr="Can only use 'private' for types">protected</error> alias A = Int32

private lib A
end

<error descr="Can only use 'private' for types">protected</error> lib A
end

private macro foo
end

<error descr="Can only use 'private' for macros">protected</error> macro foo
end