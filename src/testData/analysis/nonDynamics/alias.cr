alias X1 = Int32

(alias X2 = Int32)

begin
  alias X3 = Int32
end

A = alias X4 = Int32

a = alias <error descr="Can't declare alias dynamically">X5</error> = Int32

if true
  alias <error descr="Can't declare alias dynamically">X6</error> = Int32
else
  alias <error descr="Can't declare alias dynamically">X7</error> = Int32
end

while true
  alias <error descr="Can't declare alias dynamically">X8</error> = Int32
end

class A
  alias X9 = Int32
end