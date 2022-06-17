class A

end

class A

end

struct <error descr="'A' is already defined as class and can't be reopened as struct">A</error>

end

module <error descr="'A' is already defined as class and can't be reopened as module">A</error>

end

enum <error descr="'A' is already defined as class and can't be reopened as enum">A</error>
  X
  Y
  Z
end

annotation <error descr="'A' is already defined as class and can't be reopened as annotation">A</error>
end

lib <error descr="'A' is already defined as class and can't be reopened as library">A</error>

end

alias <error descr="'A' is already defined">A</error> = Int32

A = 1