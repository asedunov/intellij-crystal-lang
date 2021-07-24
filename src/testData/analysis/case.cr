<error descr="Exhaustive 'case' expression (case ... in) requires a condition">case</error>
in false then 0
in true then 0
end

case x
in false then 0
in true then 0
end

case x
in false then 0
in true then 0
<error descr="Exhaustive 'case' expression doesn't allow an 'else'">else</error> 0
end

case x
in false then 0
<error descr="Expected 'in', not 'when'">when</error> true then 0
end

case x
when false then 0
<error descr="Expected 'when', not 'in'">in</error> true then 0
end

case x
when false then 0
when true then 0
end

case {x, y}
when z then 0
when {0, 0} then 0
when <error descr="Wrong number of tuple elements (given 3, expected 2)">{1, 2, 3}</error> then 0
end

case x
when y then 0
when {0, 0} then 0
end

case x
when <error descr="'when _' is not supported, use 'else' block instead">_</error> then 0
when {_, 1} then 0
end

case {x, y}
when <error descr="'when _' is not supported, use 'else' block instead">_</error> then 0
when {_, 1} then 0
end

case x
in nil then 0
in false then 0
in true then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">123</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">"123"</error> then 0
in X then 0
in X::Y then 0
in X(Y, Z) then 0
in <error descr="'when _' is not supported">_</error> then 0
in X.class then 0
in X::Y.class then 0
in X(Y, Z).class then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">y</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">y.z</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">y(1, 2)</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">y.z(1, 2)</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">y.z?</error> then 0
in .z? then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">.z</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">.z(1)</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">.z?(1)</error> then 0
in <error descr="Invalid 'in'-condition for an exhaustive 'case'">{false, true}</error> then 0
end

case {x, y}
in {.z?, false}
in {true, _}
in {<error descr="Invalid 'in'-condition for an exhaustive 'case'">z?</error>, X}
in {X::Y, Y.class}
end