module X
end

(module X
 end)

begin
  module X
  end
end

A = module X
end

a = module <error descr="Can't declare module dynamically">X</error>
end

if true
  module <error descr="Can't declare module dynamically">X</error>
  end
else
  module <error descr="Can't declare module dynamically">X</error>
  end
end

while true
  module <error descr="Can't declare module dynamically">X</error>
  end
end

class C
  module X
  end
end