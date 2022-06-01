# LANGUAGE_LEVEL: 1.0

case {*1}
when {2}; 3; end

case {*1, 2, *3}
when {1, 2, 3}; 0; end

case 1
when {*2}; 3; end