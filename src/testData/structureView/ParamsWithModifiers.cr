def foo(*)
end

def foo(*x)
end

def foo(*x : Int)
end

def foo(**)
end

def foo(**x)
end

def foo(**x : Int)
end

def foo(&)
end

def foo(& : Int -> Int)
end