lib L
  union A

  end
end

lib L
  type <error descr="'A' is already defined">A</error> = Int32

  struct <error descr="'A' is already defined">A</error>

  end

  union <error descr="'A' is already defined">A</error>

  end
end