class X
end

(class X
 end)

begin
  class X
  end
end

A = class X
end

a = class <error descr="Can't declare class dynamically">X</error>
end

if true
  class <error descr="Can't declare class dynamically">X</error>
  end
else
  class <error descr="Can't declare class dynamically">X</error>
  end
end

while true
  class <error descr="Can't declare class dynamically">X</error>
  end
end

class A
  class X
  end
end