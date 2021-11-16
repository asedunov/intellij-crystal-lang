foo(1) {}

foo(1) do
end

foo(&x) <error descr="Multiple block arguments are not allowed">{}</error>

foo(&x) <error descr="Multiple block arguments are not allowed">do
end</error>

foo(&x, <error descr="Multiple block arguments are not allowed">&y</error>)

foo(&x, <error descr="Multiple block arguments are not allowed">&y</error>) <error descr="Multiple block arguments are not allowed">{}</error>

foo(&x, <error descr="Multiple block arguments are not allowed">&y</error>) <error descr="Multiple block arguments are not allowed">do
end</error>