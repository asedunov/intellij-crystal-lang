annotation A
end

@[A(<error descr="Duplicated named argument name: foo">foo</error>: 1, bar: 2, <error descr="Duplicated named argument name: foo">foo</error>: 3)]
class X

end