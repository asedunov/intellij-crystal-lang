enum X1
  E
end

(enum X2
   E
 end)

begin
  enum X3
    E
  end
end

A = enum X4
  E
end

a = enum <error descr="Can't declare enum dynamically">X5</error>
  E
end

if true
  enum <error descr="Can't declare enum dynamically">X6</error>
    E
  end
else
  enum <error descr="Can't declare enum dynamically">X7</error>
    E
  end
end

while true
  enum <error descr="Can't declare enum dynamically">X8</error>
    E
  end
end

class A
  enum X9
    E
  end
end