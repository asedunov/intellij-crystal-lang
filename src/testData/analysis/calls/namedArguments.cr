foo(x: 1, y: 2)

foo(<error descr="Duplicated named argument name: x">x</error>: 1, y: 2, <error descr="Duplicated named argument name: x">x</error>: 3)

foo(<error descr="Duplicated named argument name: x">x</error>: 1, y: 2, <error descr="Duplicated named argument name: x">"x"</error>: 3)

foo(<error descr="Duplicated named argument name: x">"x"</error>: 1, y: 2, <error descr="Duplicated named argument name: x">"x"</error>: 3)