def foo=(x : Int32)
end

def foo=(x : Int32, <error descr="Setter method 'foo=' cannot have more than one parameter">y : Int32</error>)
end

def foo=(<error descr="Setter method 'foo=' cannot have more than one parameter">*x : Int32</error>)
end

def foo=(<error descr="Setter method 'foo=' cannot have more than one parameter">**x : Int32</error>)
end

def foo=(x : Int32, <error descr="Setter method 'foo=' cannot have more than one parameter">&block</error>)
end

def foo=(<error descr="Setter method 'foo=' cannot have a block">&block</error>)
end

def []=(x : Int32)
end

def []=(x : Int32, y : Int32)
end

def []=(*x : Int32)
end

def []=(**x : Int32)
end

def []=(x : Int32, <error descr="Setter method '[]=' cannot have a block">&block</error>)
end

def []=(<error descr="Setter method '[]=' cannot have a block">&block</error>)
end
