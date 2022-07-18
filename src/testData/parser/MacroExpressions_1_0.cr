{{ foo.nil? }}

{{ foo &.nil? }}

{{ foo.nil?(foo) }}

{{ nil?(foo) }}

macro mmm
  macro foo(name)
    # some comments \{{ name.id }}
  end
  macro bar(name)
    # some comments \a
  end
end