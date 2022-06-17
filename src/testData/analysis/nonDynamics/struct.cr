struct X
end

(struct X
 end)

begin
  struct X
  end
end

A = struct X
end

a = struct <error descr="Can't declare struct dynamically">X</error>
end

if true
  struct <error descr="Can't declare struct dynamically">X</error>
  end
else
  struct <error descr="Can't declare struct dynamically">X</error>
  end
end

while true
  struct <error descr="Can't declare struct dynamically">X</error>
  end
end

class C
  struct X
  end
end