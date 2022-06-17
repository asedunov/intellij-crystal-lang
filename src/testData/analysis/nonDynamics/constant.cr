X = 1

(X = 1)

begin
  X = 1
end

A = X = 1

a = <error descr="Can't declare constant dynamically">X</error> = 1

if true
  <error descr="Can't declare constant dynamically">X</error> = 1
else
  <error descr="Can't declare constant dynamically">X</error> = 1
end

while true
  <error descr="Can't declare constant dynamically">X</error> = 1
end

class C
  X = 1
end