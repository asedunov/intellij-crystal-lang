lib L
  type A = Int32
end

lib L
  type <error descr="'A' is already defined">A</error> = Int32

  struct <error descr="'A' is already defined">A</error>

  end

  union <error descr="'A' is already defined">A</error>

  end

  A = 1
end