require "my.cr"

(require "my.cr")

begin
  require "my.cr"
end

A = require "my.cr"

a = <error descr="Can't use 'require' dynamically">require "my.cr"</error>

if true
  <error descr="Can't use 'require' dynamically">require "my.cr"</error>
else
  <error descr="Can't use 'require' dynamically">require "my.cr"</error>
end

while true
  <error descr="Can't use 'require' dynamically">require "my.cr"</error>
end

class A
  <error descr="Can't use 'require' in type body">require "my.cr"</error>
end