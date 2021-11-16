def foo
end

(def foo
 end)

begin
  def foo
  end
end

A = def foo
end

a = def <error descr="Can't declare method dynamically">foo</error>
end

if true
  def <error descr="Can't declare method dynamically">foo</error>
  end
else
  def <error descr="Can't declare method dynamically">foo</error>
  end
end

while true
  def <error descr="Can't declare method dynamically">foo</error>
  end
end

class A
  def foo
  end
end