case 1; when 1; 2; else; 3; end

case 1; when 0, 1; 2; else; 3; end

case 1
when 1
2
else
3
end

case 1
when 1
2
end

case 1; when 1 then 2; else; 3; end

case 1; when x then 2; else; 3; end

case 1
when 1
2
end
if a
end

case
1
when 1
2
end
if a
end

case 1
when .foo
2
end

case 1
when .responds_to?(:foo)
2
end

case 1
when .is_a?(T)
2
end

case 1
when .as(T)
2
end

case 1
when .as?(T)
2
end

case 1
when .!()
2
end

case when 1
2
end

case
when 1
2
end

case {1, 2}
when {3, 4}
5
end

case {1, 2}
when {3, 4}, {5, 6}
7
end

case {1, 2}
when {.foo, .bar}
5
end

case {1, 2}
when foo
5
end

case a
when b
1 / 2
else
1 / 2
end

case a
when b
/ /
else
/ /
end

case {1, 2}; when {3}; 4; end

case 1; end

case foo; end

case
end

case;end

case 1
else
2
end

a = 1
case 1
when a then 1
end

case
when true
1
end

case;when true;1;end

case 1
in Int32; 2; end

case 1
in Int32.class; 2; end

case 1
in Foo(Int32); 2; end

case 1
in false; 2; end

case 1
in true; 2; end

case 1
in nil; 2; end

case 1
in .bar?; 2; end

case {1}
in {Int32}; 2; end

case {1}
in {Int32.class}; 2; end

case {1}
in {Foo(Int32)}; 2; end

case {1}
in {false}; 2; end

case {1}
in {true}; 2; end

case {1}
in {nil}; 2; end

case {1}
in {.bar?}; 2; end

case {1}
in {_}; 2; end

case 1
in Int32; 2; when 2

case 1
when Int32; 2; in 2

case 1
in Int32; 2; else

case 1
in 1; 2

case 1
in .nil?; 2

case 1
in _;

case 1; when 2 then /foo/; end

select
when foo
2
end

select
when foo
2
when bar
4
end

select
when foo
2
else
3
end

select
when 1
2
end

case :foo; when :bar; 2; end

case when .foo? then 1; end

case x; when nil; 2; when nil; end

case x; when true; 2; when true; end

case x; when 1; 2; when 1; end

case x; when 'a'; 2; when 'a'; end

case x; when "a"; 2; when "a"; end

case x; when :a; 2; when :a; end

case x; when {1, 2}; 2; when {1, 2}; end

case x; when [1, 2]; 2; when [1, 2]; end

case x; when 1..2; 2; when 1..2; end

case x; when /x/; 2; when /x/; end

case x; when X; 2; when X; end

case x; when _; end

case x; when 1; when _; end

case x; when 1, _; end