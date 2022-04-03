# INDEX: CrystalTypeFqNameIndex
# KEY: A

<caret>annotation A
end

<caret>annotation ::A
end

annotation B::A
end

annotation <caret>A::B
end

module M
  annotation A
  end

  <caret>annotation ::A
  end
end