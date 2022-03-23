"foo"
""
"hello \
     world"


%Q{hello \\n world}


%q{hello \\n world}


%q{hello \#{foo} world}


/foo/


/foo/i


/foo/m


/foo/x


/foo/imximx


/fo\\so/


/fo#{1}o/


/(fo#{"bar"}#{1}o)/


%r(foo(bar))


/ /


/=/


/ hi /


a == / /


/ /; / /


/ /
/ /


a = / /


(/ /)


a = /=/


a; if / /; / /; elsif / /; / /; end


a; if / /
/ /
elsif / /
/ /
end


a; unless / /; / /; else; / /; end


a
unless / /
/ /
else
/ /
end


a
while / /; / /; end


a
while / /
/ /
end


[/ /, / /]


{/ / => / /, / / => / /}


{/ /, / /}


begin; / /; end


begin
/ /
end


/\//


/\ /


%r(/)


%r(\/)


%r(\ )


foo /a/


foo(/a/)


foo(//)


foo(regex: //)


foo(/ /)


foo(/ /, / /)


foo a, / /


foo /;/


case / /; when / /; / /; else; / /; end


case / /
when / /
/ /
else
/ /
end


def foo; / /; end


"foo#{bar}baz"


qux "foo#{bar do end}baz"


"#{1
}"


"foo#{"bar"}baz"


`foo`


`foo#{1}bar`


`foo\``


%x(`which(foo)`)


"hello " \
 "world"


"hello "\
"world"


"hello #{1}" \
 "#{2} world"


/foo)/


"\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:"


"\123\12\1\1238\129\18\123456"


"\x\x1\x2B\xABC"


"\u\u1\u12\u123\u1AB4\u1ABCD"


"\u{123}\u{12 234 FAB}\u{1234567}"


"a\
b"


"a\
 b"


"a#{1}b"


%(\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:)


%(\123\12\1\1238\129\18\123456)


%(\x\x1\x2B\xABC)


%(\u\u1\u12\u123\u1AB4\u1ABCD)


%(\u{123}\u{12 234 FAB}\u{1234567})


%(a\
b)


%(a\
 b)


%(foo(bar))


%(foo[bar)


%(foo(bar)


%(foo\(bar)


%(a#{1}b)


%Q(\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:)


%Q(\123\12\1\1238\129\18\123456)


%Q(\x\x1\x2B\xABC)


%Q(\u\u1\u12\u123\u1AB4\u1ABCD)


%Q(\u{123}\u{12 234 FAB}\u{1234567})


%Q(a\
b)


%Q(a\
 b)


%Q(foo(bar))


%Q(foo[bar)


%Q(foo(bar)


%Q(foo\(bar)


%Q(a#{1}b)


%[\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:]


%[\123\12\1\1238\129\18\123456]


%[\x\x1\x2B\xABC]


%[\u\u1\u12\u123\u1AB4\u1ABCD]


%[\u{123}\u{12 234 FAB}\u{1234567}]


%[a\
b]


%[a\
 b]


%[foo[bar]]


%[foo(bar]


%[foo[bar]


%[foo\[bar]


%[a#{1}b]


%Q[\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:]


%Q[\123\12\1\1238\129\18\123456]


%Q[\x\x1\x2B\xABC]


%Q[\u\u1\u12\u123\u1AB4\u1ABCD]


%Q[\u{123}\u{12 234 FAB}\u{1234567}]


%Q[a\
b]


%Q[a\
 b]


%Q[foo[bar]]


%Q[foo(bar]


%Q[foo[bar]


%Q[foo\[bar]


%Q[a#{1}b]


%{\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:}


%{\123\12\1\1238\129\18\123456}


%{\x\x1\x2B\xABC}


%{\u\u1\u12\u123\u1AB4\u1ABCD}


%{\u{123}\u{12 234 FAB}\u{1234567}}


%{a\
b}


%{a\
 b}


%{foo{bar}}


%{foo(bar}


%{foo{bar}


%{foo\{bar}


%{a#{1}b}


%Q{\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:}


%Q{\123\12\1\1238\129\18\123456}


%Q{\x\x1\x2B\xABC}


%Q{\u\u1\u12\u123\u1AB4\u1ABCD}


%Q{\u{123}\u{12 234 FAB}\u{1234567}}


%Q{a\
b}


%Q{a\
 b}


%Q{foo{bar}}


%Q{foo(bar}


%Q{foo{bar}


%Q{foo\{bar}


%Q{a#{1}b}


%<\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:>


%<\123\12\1\1238\129\18\123456>


%<\x\x1\x2B\xABC>


%<\u\u1\u12\u123\u1AB4\u1ABCD>


%<\u{123}\u{12 234 FAB}\u{1234567}>


%<a\
b>


%<a\
 b>


%<foo<bar>>


%<foo[bar>


%<foo<bar>


%<foo\<bar>


%<a#{1}b>


%Q<\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:>


%Q<\123\12\1\1238\129\18\123456>


%Q<\x\x1\x2B\xABC>


%Q<\u\u1\u12\u123\u1AB4\u1ABCD>


%Q<\u{123}\u{12 234 FAB}\u{1234567}>


%Q<a\
b>


%Q<a\
 b>


%Q<foo<bar>>


%Q<foo[bar>


%Q<foo<bar>


%Q<foo\<bar>


%Q<a#{1}b>


%|\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:|


%|\123\12\1\1238\129\18\123456|


%|\x\x1\x2B\xABC|


%|\u\u1\u12\u123\u1AB4\u1ABCD|


%|\u{123}\u{12 234 FAB}\u{1234567}|


%|a\
b|


%|a\
 b|


%|foo|bar||


%|foo[bar|


%|foo|bar|


%|foo\|bar|


%|a#{1}b|


%Q|\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:|


%Q|\123\12\1\1238\129\18\123456|


%Q|\x\x1\x2B\xABC|


%Q|\u\u1\u12\u123\u1AB4\u1ABCD|


%Q|\u{123}\u{12 234 FAB}\u{1234567}|


%Q|a\
b|


%Q|a\
 b|


%Q|foo|bar||


%Q|foo[bar|


%Q|foo|bar|


%Q|foo\|bar|


%Q|a#{1}b|


%q(\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:)


%q(\123\12\1\1238\129\18\123456)


%q(\x\x1\x2B\xABC)


%q(\u\u1\u12\u123\u1AB4\u1ABCD)


%q(\u{123}\u{12 234 FAB}\u{1234567})


%q(a\
b)


%q(a\
 b)


%q(foo(bar))


%q(foo\(bar))


%q(foo[bar)


%q(foo(bar)


%q(a#{1}b)


%q[\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:]


%q[\123\12\1\1238\129\18\123456]


%q[\x\x1\x2B\xABC]


%q[\u\u1\u12\u123\u1AB4\u1ABCD]


%q[\u{123}\u{12 234 FAB}\u{1234567}]


%q[a\
b]


%q[a\
 b]


%q[foo[bar]]


%q[foo\[bar]]


%q[foo(bar]


%q[foo[bar]


%q[a#{1}b]


%q{\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:}


%q{\123\12\1\1238\129\18\123456}


%q{\x\x1\x2B\xABC}


%q{\u\u1\u12\u123\u1AB4\u1ABCD}


%q{\u{123}\u{12 234 FAB}\u{1234567}}


%q{a\
b}


%q{a\
 b}


%q{foo{bar}}


%q{foo\{bar}}


%q{foo[bar}


%q{foo{bar}


%q{a#{1}b}


%q<\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:>


%q<\123\12\1\1238\129\18\123456>


%q<\x\x1\x2B\xABC>


%q<\u\u1\u12\u123\u1AB4\u1ABCD>


%q<\u{123}\u{12 234 FAB}\u{1234567}>


%q<a\
b>


%q<a\
 b>


%q<foo<bar>>


%q<foo\<bar>>


%q<foo(bar>


%q<foo<bar>


%q<a#{1}b>


%q|\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:|


%q|\123\12\1\1238\129\18\123456|


%q|\x\x1\x2B\xABC|


%q|\u\u1\u12\u123\u1AB4\u1ABCD|


%q|\u{123}\u{12 234 FAB}\u{1234567}|


%q|a\
b|


%q|a\
 b|


%q|foo|bar||


%q|foo(bar|


%q|foo|bar|


%q|foo\|bar|


%q|a#{1}b|


`\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:`


`\123\12\1\1238\129\18\123456`


`\x\x1\x2B\xABC`


`\u\u1\u12\u123\u1AB4\u1ABCD`


`\u{123}\u{12 234 FAB}\u{1234567}`


`a\
b`


`a\
 b`


`a#{1}b`


%x(\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:)


%x(\123\12\1\1238\129\18\123456)


%x(\x\x1\x2B\xABC)


%x(\u\u1\u12\u123\u1AB4\u1ABCD)


%x(\u{123}\u{12 234 FAB}\u{1234567})


%x(a\
b)


%x(a\
 b)


%x(foo(bar))


%x(foo[bar)


%x(foo(bar)


%x(foo\(bar)


%x(a#{1}b)


%x[\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:]


%x[\123\12\1\1238\129\18\123456]


%x[\x\x1\x2B\xABC]


%x[\u\u1\u12\u123\u1AB4\u1ABCD]


%x[\u{123}\u{12 234 FAB}\u{1234567}]


%x[a\
b]


%x[a\
 b]


%x[foo[bar]]


%x[foo(bar]


%x[foo[bar]


%x[foo\[bar]


%x[a#{1}b]


%x{\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:}


%x{\123\12\1\1238\129\18\123456}


%x{\x\x1\x2B\xABC}


%x{\u\u1\u12\u123\u1AB4\u1ABCD}


%x{\u{123}\u{12 234 FAB}\u{1234567}}


%x{a\
b}


%x{a\
 b}


%x{foo{bar}}


%x{foo(bar}


%x{foo{bar}


%x{foo\{bar}


%x{a#{1}b}


%x<\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:>


%x<\123\12\1\1238\129\18\123456>


%x<\x\x1\x2B\xABC>


%x<\u\u1\u12\u123\u1AB4\u1ABCD>


%x<\u{123}\u{12 234 FAB}\u{1234567}>


%x<a\
b>

%x<a\
 b>


%x<foo<bar>>


%x<foo[bar>


%x<foo<bar>


%x<foo\<bar>


%x<a#{1}b>


%x|\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:|


%x|\123\12\1\1238\129\18\123456|


%x|\x\x1\x2B\xABC|


%x|\u\u1\u12\u123\u1AB4\u1ABCD|


%x|\u{123}\u{12 234 FAB}\u{1234567}|


%x|a\
b|


%x|a\
 b|


%x|foo|bar||


%x|foo[bar|


%x|foo|bar|


%x|foo\|bar|


%x|a#{1}b|


/\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:/


/\123\12\1\1238\129\18\123456/


/\x\x1\x2B\xABC/


/\u\u1\u12\u123\u1AB4\u1ABCD/


/\u{123}\u{12 234 FAB}\u{1234567}/


/a\
b/


/a\
 b/


/(foo(bar))/


/(foo\(bar)/


/a#{1}b/


%r(\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:)


%r(\123\12\1\1238\129\18\123456)


%r(\x\x1\x2B\xABC)


%r(\u\u1\u12\u123\u1AB4\u1ABCD)


%r(\u{123}\u{12 234 FAB}\u{1234567})


%r(a\
b)


%r(a\
 b)


%r(foo(bar))


%r(foo[bar)


%r(foo(bar)


%r(foo\(bar)


%r(a#{1}b)


%r[\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:]


%r[\123\12\1\1238\129\18\123456]


%r[\x\x1\x2B\xABC]


%r[\u\u1\u12\u123\u1AB4\u1ABCD]


%r[\u{123}\u{12 234 FAB}\u{1234567}]


%r[a\
b]


%r[a\
 b]


%r[foo[bar]]


%r[foo(bar]


%r[foo[bar]


%r[foo\[bar]


%r[a#{1}b]


%r{\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:}


%r{\123\12\1\1238\129\18\123456}


%r{\x\x1\x2B\xABC}


%r{\u\u1\u12\u123\u1AB4\u1ABCD}


%r{\u{123}\u{12 234 FAB}\u{1234567}}


%r{a\
b}


%r{a\
 b}


%r{foo{bar}}


%r{foo(bar}


%r{foo{bar}


%r{foo\{bar}


%r{a#{1}b}


%r<\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:>


%r<\123\12\1\1238\129\18\123456>


%r<\x\x1\x2B\xABC>


%r<\u\u1\u12\u123\u1AB4\u1ABCD>


%r<\u{123}\u{12 234 FAB}\u{1234567}>


%r<a\
b>


%r<a\
 b>


%r<foo<bar>>


%r<foo[bar>


%r<foo<bar>


%r<foo\<bar>


%r<a#{1}b>


%r|\a\b\e\f\n\r\t\v\\\"\z\s\(\)\[\]\{\}\<\>\|\.\:|


%r|\123\12\1\1238\129\18\123456|


%r|\x\x1\x2B\xABC|


%r|\u\u1\u12\u123\u1AB4\u1ABCD|


%r|\u{123}\u{12 234 FAB}\u{1234567}|


%r|a\
b|


%r|a\
 b|


%r|foo|bar||


%r|foo(bar|


%r|foo|bar|


%r|foo\|bar|


%r|a#{1}b|


%x(


%r(


%q(


%Q(


"abc\u{4A 4B 4C}"


%(abc\u{4A 4B 4C})


%[abc\u{4A 4B 4C}]


%{abc\u{4A 4B 4C}}


%<abc\u{4A 4B 4C}>


%|abc\u{4A 4B 4C}|


%Q(abc\u{4A 4B 4C})


%Q[abc\u{4A 4B 4C}]


%Q{abc\u{4A 4B 4C}}


%Q<abc\u{4A 4B 4C}>


%Q|abc\u{4A 4B 4C}|


%q(abc\u{4A 4B 4C})


%q[abc\u{4A 4B 4C}]


%q{abc\u{4A 4B 4C}}


%q<abc\u{4A 4B 4C}>


%q|abc\u{4A 4B 4C}|


/\#{1}/


%r(\#{1})


%r[\#{1}]


%r{\#{1}}


%r<\#{1}>


%r|\#{1}|


%r(1)i


%r[1]i


%r{1}i


%r<1>i


%r|1|i


%r(1)m


%r[1]m


%r{1}m


%r<1>m


%r|1|m


%r(1)x


%r[1]x


%r{1}x


%r<1>x


%r|1|x


%r(1)imx


%r[1]imx


%r{1}imx


%r<1>imx


%r|1|imx


1
1
/fox/