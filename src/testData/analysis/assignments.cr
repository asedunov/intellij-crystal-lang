A = 1

X::A = 1

def foo
  <error descr="Can't declare constant in method/function body">A</error> = 1
end

fun foo
  <error descr="Can't declare constant in method/function body">A</error> = 1
end

<error descr="Multiple assignment is not allowed for constants">A</error>, <error descr="Multiple assignment is not allowed for constants">B</error> = 1, 2

a, <error descr="Multiple assignment is not allowed for constants">B</error> = 1, 2

<error descr="Multiple assignment is not allowed for constants">A</error>, b = 1, 2

<error descr="Multiple assignment is not allowed for constants">::A</error>, <error descr="Multiple assignment is not allowed for constants">X::B</error> = 1, 2

<error descr="Can't reassign to constant">A</error> += 1

<error descr="Can't reassign to constant">X::A</error> -= 1

a, b = x, y

a, b = x

<error descr="Multiple assignment count mismatch">a = x, y</error>

<error descr="Multiple assignment count mismatch">a, b = x, y, z</error>

a, b <error descr="Combined multiple assignments are not allowed">+=</error> 1, 2

<error descr="Assignment is now allowed for ?/! method calls">a.foo?</error> = 1

<error descr="Assignment is now allowed for ?/! method calls">a.foo?</error> += 1

a.foo! = 1

a.foo! += 1

<error descr="Can't change the value of self">self</error> = 1

<error descr="Can't change the value of self">self</error> += 1

a.b = 1

a.b += 1

@b = 1

@b += 1

@@b = 1

@@b += 1

<error descr="Global variables are not supported, use class variables instead">$a</error> = 1

<error descr="Global variables are not supported, use class variables instead">$a</error> += 1

a[1, 2] = 3

a[1, 2] += 3

a.[]=(1, 2)

a.foo=(*x)

<error descr="This expression is not allowed as assignment left-hand side">a(1, 2)</error> = 3

<error descr="This expression is not allowed as assignment left-hand side">a(1, 2)</error> += 3

<error descr="This expression is not allowed as assignment left-hand side">1</error> = 2

<error descr="This expression is not allowed as assignment left-hand side">1</error> += 2