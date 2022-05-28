# LANGUAGE_LEVEL: 1.0

case {*1}
when <error descr="Wrong number of tuple elements (given 1, expected 0)">{2}</error>; 3; end

case {*1, 2, *3}
when <error descr="Wrong number of tuple elements (given 3, expected 1)">{1, 2, 3}</error>; 0; end

case 1
when {*2}; 3; end