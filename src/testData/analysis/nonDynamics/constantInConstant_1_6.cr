# LANGUAGE_LEVEL: 1.6

A = B = 1
A = (B = 1)
A = foo(<error descr="Can't declare constant dynamically">B</error> = 1)
A = foo { B = 1 }
A = begin; B = 1; end
A = begin; 1; rescue; <error descr="Can't declare constant dynamically">B</error> = 1; end
A = begin; 1; rescue; 1; else; <error descr="Can't declare constant dynamically">B</error> = 1; end
A = begin; 1; ensure; <error descr="Can't declare constant dynamically">B</error> = 1; end