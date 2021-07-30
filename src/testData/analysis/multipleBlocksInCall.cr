foo(1) {}

foo(1) do
end

foo(&x) <error descr="Can't use captured and non-captured blocks together">{}</error>

foo(&x) <error descr="Can't use captured and non-captured blocks together">do
end</error>

foo(&x, <error descr="Can't use captured and non-captured blocks together">&y</error>)

foo(&x, <error descr="Can't use captured and non-captured blocks together">&y</error>) <error descr="Can't use captured and non-captured blocks together">{}</error>

foo(&x, <error descr="Can't use captured and non-captured blocks together">&y</error>) <error descr="Can't use captured and non-captured blocks together">do
end</error>