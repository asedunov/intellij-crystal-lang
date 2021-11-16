enum X
  E
end

(enum X
   E
 end)

begin
  enum X
    E
  end
end

A = enum X
  E
end

a = enum <error descr="Can't declare enum dynamically">X</error>
  E
end

if true
  enum <error descr="Can't declare enum dynamically">X</error>
    E
  end
else
  enum <error descr="Can't declare enum dynamically">X</error>
    E
  end
end

while true
  enum <error descr="Can't declare enum dynamically">E</error>
    E
  end
end

class A
  enum X
    E
  end
end