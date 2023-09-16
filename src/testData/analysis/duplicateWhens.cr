case x; when <error descr="Duplicate when condition 'nil' in case">nil</error>; 2; when <error descr="Duplicate when condition 'nil' in case">nil</error>; end

case x; when <error descr="Duplicate when condition 'true' in case">true</error>; 2; when <error descr="Duplicate when condition 'true' in case">true</error>; end

case x; when <error descr="Duplicate when condition '1' in case">1</error>; 2; when <error descr="Duplicate when condition '1' in case">1</error>; end

case x; when <error descr="Duplicate when condition ''a'' in case">'a'</error>; 2; when <error descr="Duplicate when condition ''a'' in case">'a'</error>; end

case x; when <error descr="Duplicate when condition '\"a\"' in case">"a"</error>; 2; when <error descr="Duplicate when condition '\"a\"' in case">"a"</error>; end

case x; when <error descr="Duplicate when condition ':a' in case">:a</error>; 2; when <error descr="Duplicate when condition ':a' in case">:a</error>; end

case x; when <error descr="Duplicate when condition '{1, 2}' in case">{1, 2}</error>; 2; when <error descr="Duplicate when condition '{1, 2}' in case">{1, 2}</error>; end

case x; when <error descr="Duplicate when condition '[1, 2]' in case">[1, 2]</error>; 2; when <error descr="Duplicate when condition '[1, 2]' in case">[1, 2]</error>; end

case x; when <error descr="Duplicate when condition '1..2' in case">1..2</error>; 2; when <error descr="Duplicate when condition '1..2' in case">1..2</error>; end

case x; when <error descr="Duplicate when condition '/x/' in case">/x/</error>; 2; when <error descr="Duplicate when condition '/x/' in case">/x/</error>; end

case x; when <error descr="Duplicate when condition 'X' in case">X</error>; 2; when <error descr="Duplicate when condition 'X' in case">X</error>; end