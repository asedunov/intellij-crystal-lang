alias X = Int32

(alias X = Int32)

begin
  alias X = Int32
end

A = alias X = Int32

a = alias <error descr="Can't declare alias dynamically">X</error> = Int32

if true
  alias <error descr="Can't declare alias dynamically">X</error> = Int32
else
  alias <error descr="Can't declare alias dynamically">X</error> = Int32
end

while true
  alias <error descr="Can't declare alias dynamically">X</error> = Int32
end

class A
  alias X = Int32
end