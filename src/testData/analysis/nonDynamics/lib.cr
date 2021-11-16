lib X
end

(lib X
 end)

begin
  lib X
  end
end

A = lib X
end

a = lib <error descr="Can't declare library dynamically">X</error>
end

if true
  lib <error descr="Can't declare library dynamically">X</error>
  end
else
  lib <error descr="Can't declare library dynamically">X</error>
  end
end

while true
  lib <error descr="Can't declare library dynamically">X</error>
  end
end

class A
  lib X
  end
end