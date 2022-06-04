enum A
  X
  Y
  Z
end

enum A

end

class <error descr="'A' is already defined as enum and can't be reopened as class">A</error>

end

struct <error descr="'A' is already defined as enum and can't be reopened as struct">A</error>

end

module <error descr="'A' is already defined as enum and can't be reopened as module">A</error>

end

annotation <error descr="'A' is already defined as enum and can't be reopened as annotation">A</error>
end

lib <error descr="'A' is already defined as enum and can't be reopened as library">A</error>

end

alias <error descr="'A' is already defined">A</error> = Int32