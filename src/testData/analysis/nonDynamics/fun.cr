fun foo
end

(fun foo
 end)

begin
  fun foo
  end
end

A = fun foo
end

a = fun <error descr="Can't declare function dynamically">foo</error>
end

if true
  fun <error descr="Can't declare function dynamically">foo</error>
  end
else
  fun <error descr="Can't declare function dynamically">foo</error>
  end
end

while true
  fun <error descr="Can't declare function dynamically">foo</error>
  end
end

class A
  fun foo
  end
end