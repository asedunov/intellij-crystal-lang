macro foo;end

macro [];end

macro foo; 1 + 2; end

macro foo(x); 1 + 2; end

macro foo(x)
 1 + 2; end

macro foo(**args)
1
end

macro foo(x, *); 1; end

macro foo(**x, **y); end

macro foo(**x, y); end

macro foo; 1 + 2 {{foo}} 3 + 4; end

macro foo; 1 + 2 {{ foo }} 3 + 4; end

macro foo; 1 + 2 {{foo}}\ 3 + 4; end

macro foo(
a = 0
)
end

macro foo
eenum
end

macro foo
'\''
end

macro foo
'\\'
end

macro foo
"\'"
end

macro foo
"\\"
end

macro foo; {% foo = 1 }; end

macro def foo : String; 1; end

macro foo=;end

macro Foo;end

macro foo.bar;end

macro Foo.bar;end

macro foo&&;end

macro foo

macro foo(x
,y); 1; end

macro foo(x : Int32); end

macro foo(*x, *y); end

macro foo(x, *y);end

macro foo x y; end

macro foo(x y); end

macro foo(x y z); end

macro foo *y;end

macro foo x; 1 + 2; end

macro foo x
 1 + 2; end

macro foo(x = __LINE__);end

macro foo;{%end};end

macro foo;%var if true;end

macro foo;var if true;end

macro foo;if %var;true;end;end

macro foo;if var;true;end;end

macro foo;%var unless true;end

macro foo;var unless true;end

macro foo;unless %var;true;end;end

macro foo;unless var;true;end;end

macro x
%{}
end

enum Foo; macro foo;end; end

macro foo
  <<-FOO
    #{ %var }
  FOO
end

macro foo
  <<-FOO, <<-BAR + ""
  FOO
  BAR
end

macro foo
  <<-FOO
    %foo
  FOO
end

macro foo(@var); end

macro foo(@@var); end

macro foo; bar classx; end

macro foo; bar class0; end

macro foo; bar class?; end

macro foo; bar class!; end

macro foo; bar class: 1; end

macro foo;bar(end: 1);end