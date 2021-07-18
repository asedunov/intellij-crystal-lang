<error descr="Can't use named tuple syntax for Hash-like literal, use '=>' instead">Foo { a: 123 }</error>

Foo { a => 123 }

Foo { 1, 2, 3 }

<error descr="Can't use named tuple syntax for Hash-like literal, use '=>' instead">Foo { "123": 123 }</error>

Foo { 123 => 123 }