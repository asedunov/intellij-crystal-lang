# LANGUAGE_LEVEL: 1.2

case {<error descr="Splat is not allowed inside case expression">*</error>1}
when {2}; 3; end

case {<error descr="Splat is not allowed inside case expression">*</error>1, 2, <error descr="Splat is not allowed inside case expression">*</error>3}
when {1, 2, 3}; 0; end

case 1
when {*2}; 3; end