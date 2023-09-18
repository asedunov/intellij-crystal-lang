foo { |x, y| }

foo { |<error descr="Duplicated parameter name: x">x</error>, y, <error descr="Duplicated parameter name: x">x</error>| }

foo { |x, (y, z)| }

foo { |x, (<error descr="Cannot use 'if' as a parameter name">if</error>, z)| }

foo { |x, ((y, <error descr="Cannot use 'if' as a parameter name">if</error>), w)| }

foo { |<error descr="Duplicated parameter name: x">x</error>, (y, <error descr="Duplicated parameter name: x">x</error>)| }

foo { |x, <error descr="Cannot use 'if' as a parameter name">if</error>| }

foo { |*x, y, <error descr="Splat parameter is already specified">*z</error>| }

foo { |*x, y, <error descr="Splat parameter is already specified">*(z, u)</error>| }