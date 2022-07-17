# LANGUAGE_LEVEL: 1.5

fun foo(<error descr="Duplicated parameter name: x">x</error> : Int32, <error descr="Duplicated parameter name: x">x</error> : Int64); end

lib Foo; fun foo(<error descr="Duplicated parameter name: x">x</error> : Int32, <error descr="Duplicated parameter name: x">x</error> : Int64); end