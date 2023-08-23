y = 1
select
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">!x</error> then 0;
when +x then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">x && y</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">x || y</error> then 0;
when x + y then 0;
when x & y then 0;
when x | y then 0;
when x[y] then 0;
when x[] then 0;
when x(y) then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">$1</error> then 0;
when `abc` then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">"abc"</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">123</error> then 0;
when x then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">y</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">@x</error> then 0;
when a.x then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">a.@x</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">x = 1</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">@x = 1</error> then 0;
when a.x = 1 then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">a.@x = 1</error> then 0;
when x[y] = 1 then 0;
when x[] = 1 then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">x += 1</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">a.x += 1</error> then 0;
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">x[y] += 1</error> then 0;
when a = x.y
when a = x(y)
when a = `123`
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">a += x.y</error>
when <error descr="invalid 'when' expression in 'select': must be an assignment or call">a += x(y)</error>
end