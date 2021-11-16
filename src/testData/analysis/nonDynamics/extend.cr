extend X

(extend X)

begin
  extend X
end

A = extend X

a = <error descr="Can't use 'extend' dynamically">extend X</error>

if true
  <error descr="Can't use 'extend' dynamically">extend X</error>
else
  <error descr="Can't use 'extend' dynamically">extend X</error>
end

while true
  <error descr="Can't use 'extend' dynamically">extend X</error>
end

class A
  extend X
end