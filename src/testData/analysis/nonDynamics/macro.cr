macro foo
end

(macro foo
 end)

begin
  macro foo
  end
end

A = macro foo
end

a = macro <error descr="Can't declare macro dynamically">foo</error>
end

if true
  macro <error descr="Can't declare macro dynamically">foo</error>
  end
else
  macro <error descr="Can't declare macro dynamically">foo</error>
  end
end

while true
  macro <error descr="Can't declare macro dynamically">foo</error>
  end
end

class A
  macro foo
  end
end