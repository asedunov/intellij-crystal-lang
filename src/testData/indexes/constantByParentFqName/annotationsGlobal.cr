# INDEX: CrystalConstantParentFqNameIndex
# KEY:

<caret>annotation A
end

<caret>annotation ::A
end

annotation <caret>B::A
end

annotation <caret>A::B
end

<caret>module M
  annotation A
  end

  <caret>annotation ::A
  end
end