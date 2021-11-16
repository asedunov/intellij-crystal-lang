include X

(include X)

begin
  include X
end

A = include X

a = <error descr="Can't use 'include' dynamically">include X</error>

if true
  <error descr="Can't use 'include' dynamically">include X</error>
else
  <error descr="Can't use 'include' dynamically">include X</error>
end

while true
  <error descr="Can't use 'include' dynamically">include X</error>
end

class A
  include X
end