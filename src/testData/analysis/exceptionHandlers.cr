begin
  #
rescue ex : MyException
  #
rescue
  #
<error descr="Catch-all rescue can only be specified once">rescue</error>
  #
end

begin
  #
rescue
  #
<error descr="Specific rescue must come before catch-all rescue">rescue</error> ex : MyException
  #
end

begin
  #
rescue ex : MyException
  #
rescue
  #
end

begin
  #
rescue
  #
else
  #
end

begin
  #
<error descr="'else' is useless without 'rescue'">else</error>
  #
end