<error descr="Tuple syntax is not supported for Hash-like literal">Foo { a: 123 }</error>

Foo { a => 123 }

Foo { 1, 2, 3 }

<error descr="Tuple syntax is not supported for Hash-like literal">Foo { "123": 123 }</error>

Foo { 123 => 123 }