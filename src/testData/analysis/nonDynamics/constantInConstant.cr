A = <error descr="Can't declare constant dynamically">B</error> = 1
A = (<error descr="Can't declare constant dynamically">B</error> = 1)
A = foo(<error descr="Can't declare constant dynamically">B</error> = 1)
A = foo { <error descr="Can't declare constant dynamically">B</error> = 1 }
A = begin; <error descr="Can't declare constant dynamically">B</error> = 1; end
A = begin; 1; rescue; <error descr="Can't declare constant dynamically">B</error> = 1; end
A = begin; 1; rescue; 1; else; <error descr="Can't declare constant dynamically">B</error> = 1; end
A = begin; 1; ensure; <error descr="Can't declare constant dynamically">B</error> = 1; end