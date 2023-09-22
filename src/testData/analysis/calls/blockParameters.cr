foo { |x, y| }

foo { |<error descr="Duplicated parameter name: x">x</error>, y, <error descr="Duplicated parameter name: x">x</error>| }

foo { |x, (y, z)| }

foo { |x, (<error descr="Cannot use 'if' as a parameter name">if</error>, z)| }

foo { |x, ((y, <error descr="Cannot use 'if' as a parameter name">if</error>), w)| }

foo { |<error descr="Duplicated parameter name: x">x</error>, (y, <error descr="Duplicated parameter name: x">x</error>)| }

foo { |x, <error descr="Cannot use 'if' as a parameter name">if</error>| }

foo { |*x, y, <error descr="Splat parameter is already specified">*z</error>| }

foo { |*x, y, <error descr="Splat parameter is already specified">*(z, u)</error>| }

foo { |a, (*x, <error descr="Splat parameter is already specified">*y</error>, z), c| }

foo { |*a, (*x, y, z), c| }

foo { |a, (*x, (y, z, *u), w), c| }

foo { |a, (*x, (y, z, u), <error descr="Splat parameter is already specified">*w</error>), c| }

foo { |a, (x, (*y, z, <error descr="Splat parameter is already specified">*u</error>), w), c| }