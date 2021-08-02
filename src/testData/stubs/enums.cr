enum E
  A
  B
  C
end

private enum E
  A
  B
  C
end

@[Foo]
enum E
  A
  B
  C
end

enum X::Y::E
  A
  B
  C
end

enum ::X::Y::E
  A
  B
  C
end

module M
    enum E
      A
      B
      C
    end

    enum X::Y::E
      A
      B
      C
    end

    enum ::X::Y::E
      A
      B
      C
    end
end