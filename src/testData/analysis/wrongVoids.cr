a = <error descr="Void value expression is not allowed here">return</error> 1
a = <error descr="Void value expression is not allowed here">break</error> 1
a = <error descr="Void value expression is not allowed here">next</error> 1

a = <error descr="Void value expression is not allowed here">return</error>
a = <error descr="Void value expression is not allowed here">break</error>
a = <error descr="Void value expression is not allowed here">next</error>

a = 1; a += <error descr="Void value expression is not allowed here">return</error>
a = 1; a += <error descr="Void value expression is not allowed here">break</error>
a = 1; a += <error descr="Void value expression is not allowed here">next</error>

yield <error descr="Void value expression is not allowed here">return</error>
yield <error descr="Void value expression is not allowed here">break</error>
yield <error descr="Void value expression is not allowed here">next</error>

foo(<error descr="Void value expression is not allowed here">return</error>)
foo(<error descr="Void value expression is not allowed here">break</error>)
foo(<error descr="Void value expression is not allowed here">next</error>)

foo[<error descr="Void value expression is not allowed here">return</error>]
foo[<error descr="Void value expression is not allowed here">break</error>]
foo[<error descr="Void value expression is not allowed here">next</error>]

foo[1] = <error descr="Void value expression is not allowed here">return</error>
foo[1] = <error descr="Void value expression is not allowed here">break</error>
foo[1] = <error descr="Void value expression is not allowed here">next</error>

if <error descr="Void value expression is not allowed here">return</error>; end
if <error descr="Void value expression is not allowed here">break</error>; end
if <error descr="Void value expression is not allowed here">next</error>; end

unless <error descr="Void value expression is not allowed here">return</error>; end
unless <error descr="Void value expression is not allowed here">break</error>; end
unless <error descr="Void value expression is not allowed here">next</error>; end

while <error descr="Void value expression is not allowed here">return</error>; end
while <error descr="Void value expression is not allowed here">break</error>; end
while <error descr="Void value expression is not allowed here">next</error>; end

until <error descr="Void value expression is not allowed here">return</error>; end
until <error descr="Void value expression is not allowed here">break</error>; end
until <error descr="Void value expression is not allowed here">next</error>; end

1 if <error descr="Void value expression is not allowed here">return</error>;
1 if <error descr="Void value expression is not allowed here">break</error>;
1 if <error descr="Void value expression is not allowed here">next</error>;

<error descr="Void value expression is not allowed here">return</error>.foo
<error descr="Void value expression is not allowed here">break</error>.foo
<error descr="Void value expression is not allowed here">next</error>.foo

<error descr="Void value expression is not allowed here">return</error>.as(Int32)
<error descr="Void value expression is not allowed here">break</error>.as(Int32)
<error descr="Void value expression is not allowed here">next</error>.as(Int32)

<error descr="Void value expression is not allowed here">return</error>.as?(Int32)
<error descr="Void value expression is not allowed here">break</error>.as?(Int32)
<error descr="Void value expression is not allowed here">next</error>.as?(Int32)

<error descr="Void value expression is not allowed here">return</error>.nill?(Int32)
<error descr="Void value expression is not allowed here">break</error>.nill?(Int32)
<error descr="Void value expression is not allowed here">next</error>.nill?(Int32)

<error descr="Void value expression is not allowed here">return</error>.is_a?(Int32)
<error descr="Void value expression is not allowed here">break</error>.is_a?(Int32)
<error descr="Void value expression is not allowed here">next</error>.is_a?(Int32)

<error descr="Void value expression is not allowed here">return</error>.responds_to?(:foo)
<error descr="Void value expression is not allowed here">break</error>.responds_to?(:foo)
<error descr="Void value expression is not allowed here">next</error>.responds_to?(:foo)

<error descr="Void value expression is not allowed here">return</error>[]
<error descr="Void value expression is not allowed here">break</error>[]
<error descr="Void value expression is not allowed here">next</error>[]

<error descr="Void value expression is not allowed here">return</error>[0]
<error descr="Void value expression is not allowed here">break</error>[0]
<error descr="Void value expression is not allowed here">next</error>[0]

<error descr="Void value expression is not allowed here">return</error>[0]= 1
<error descr="Void value expression is not allowed here">break</error>[0]= 1
<error descr="Void value expression is not allowed here">next</error>[0]= 1

<error descr="Void value expression is not allowed here">return</error> .. 1
<error descr="Void value expression is not allowed here">break</error> .. 1
<error descr="Void value expression is not allowed here">next</error> .. 1

<error descr="Void value expression is not allowed here">return</error> ... 1
<error descr="Void value expression is not allowed here">break</error> ... 1
<error descr="Void value expression is not allowed here">next</error> ... 1

1 .. <error descr="Void value expression is not allowed here">return</error>
1 .. <error descr="Void value expression is not allowed here">break</error>
1 .. <error descr="Void value expression is not allowed here">next</error>

1 ... <error descr="Void value expression is not allowed here">return</error>
1 ... <error descr="Void value expression is not allowed here">break</error>
1 ... <error descr="Void value expression is not allowed here">next</error>

<error descr="Void value expression is not allowed here">return</error> ? 1 : 2
<error descr="Void value expression is not allowed here">break</error> ? 1 : 2
<error descr="Void value expression is not allowed here">next</error> ? 1 : 2

1 ? <error descr="Void value expression is not allowed here">return</error> : 2
1 ? <error descr="Void value expression is not allowed here">break</error> : 2
1 ? <error descr="Void value expression is not allowed here">next</error> : 2

1 ? 2 : <error descr="Void value expression is not allowed here">return</error>
1 ? 2 : <error descr="Void value expression is not allowed here">break</error>
1 ? 2 : <error descr="Void value expression is not allowed here">next</error>

+<error descr="Void value expression is not allowed here">return</error>
+<error descr="Void value expression is not allowed here">break</error>
+<error descr="Void value expression is not allowed here">next</error>

<error descr="Void value expression is not allowed here">return</error> + 1
<error descr="Void value expression is not allowed here">break</error> + 1
<error descr="Void value expression is not allowed here">next</error> + 1

1 + <error descr="Void value expression is not allowed here">return</error>
1 + <error descr="Void value expression is not allowed here">break</error>
1 + <error descr="Void value expression is not allowed here">next</error>

case <error descr="Void value expression is not allowed here">return</error>; when 1; end
case <error descr="Void value expression is not allowed here">break</error>; when 1; end
case <error descr="Void value expression is not allowed here">next</error>; when 1; end

case 1; when <error descr="Void value expression is not allowed here">return</error>; end
case 1; when <error descr="Void value expression is not allowed here">break</error>; end
case 1; when <error descr="Void value expression is not allowed here">next</error>; end

[1, <error descr="Void value expression is not allowed here">return</error>, 2]
[1, <error descr="Void value expression is not allowed here">break</error>, 2]
[1, <error descr="Void value expression is not allowed here">next</error>, 2]

{1, <error descr="Void value expression is not allowed here">return</error>, 2}
{1, <error descr="Void value expression is not allowed here">break</error>, 2}
{1, <error descr="Void value expression is not allowed here">next</error>, 2}

{a: 1, b: <error descr="Void value expression is not allowed here">return</error>, c: 2}
{a: 1, b: <error descr="Void value expression is not allowed here">break</error>, c: 2}
{a: 1, b: <error descr="Void value expression is not allowed here">next</error>, c: 2}

{"a" => 1, <error descr="Void value expression is not allowed here">return</error> => 2, "b" => 3}
{"a" => 1, <error descr="Void value expression is not allowed here">break</error> => 2, "b" => 3}
{"a" => 1, <error descr="Void value expression is not allowed here">next</error> => 2, "b" => 3}

{"a" => 1, "b" => return, "c" => 2}
{"a" => 1, "b" => break, "c" => 2}
{"a" => 1, "b" => next, "c" => 2}

foo(*<error descr="Void value expression is not allowed here">return</error>)
foo(*<error descr="Void value expression is not allowed here">break</error>)
foo(*<error descr="Void value expression is not allowed here">next</error>)

a.foo=(*<error descr="Void value expression is not allowed here">return</error>)
a.foo=(*<error descr="Void value expression is not allowed here">break</error>)
a.foo=(*<error descr="Void value expression is not allowed here">next</error>)

(return)
(break)
(next)