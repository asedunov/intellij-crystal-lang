# INDEX: CrystalTypeShortNameIndex
# KEY: A

<caret>annotation A
end

annotation B
end

<caret>annotation ::A
end

annotation ::B
end

annotation A::X
end

<caret>annotation X::A
end

module M
  <caret>annotation A
  end

  annotation B
  end

  <caret>annotation ::A
  end

  annotation ::B
  end

  annotation A::X
  end

  <caret>annotation X::A
  end
end