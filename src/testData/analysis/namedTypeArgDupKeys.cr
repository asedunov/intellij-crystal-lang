Foo({<error descr="Duplicated named type argument name: x">x</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">x</error>: Int32})

Foo({<error descr="Duplicated named type argument name: x">x</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">"x"</error>: Int32})

Foo({<error descr="Duplicated named type argument name: x">"x"</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">"x"</error>: Int32})

Foo(<error descr="Duplicated named type argument name: x">x</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">x</error>: Int32)

Foo(<error descr="Duplicated named type argument name: x">x</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">"x"</error>: Int32)

Foo(<error descr="Duplicated named type argument name: x">"x"</error>: Int32, y: Int32, <error descr="Duplicated named type argument name: x">"x"</error>: Int32)