<<-FOO
foo
 foo
  foo
FOO

<<-FOO
<error descr="Heredoc line must have an indent greater than or equal to 1"></error>foo
 foo
  foo
 FOO

<<-FOO
<error descr="Heredoc line must have an indent greater than or equal to 2"></error>foo
<error descr="Heredoc line must have an indent greater than or equal to 2"> </error>foo
  foo
  FOO