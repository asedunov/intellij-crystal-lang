annotation X
end

(annotation X
 end)

begin
  annotation X
  end
end

A = annotation X
end

a = annotation <error descr="Can't declare annotation dynamically">X</error>
end

if true
  annotation <error descr="Can't declare annotation dynamically">X</error>
  end
else
  annotation <error descr="Can't declare annotation dynamically">X</error>
  end
end

while true
  annotation <error descr="Can't declare annotation dynamically">X</error>
  end
end

class A
  annotation X
  end
end