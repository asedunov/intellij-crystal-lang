def foo; yield; end

def foo; yield 1; end

def foo; yield 1; yield; end

def foo; with a yield; end

def foo; with a yield 1; end

def foo; a = 1; with a yield a; end

if foo; 1; end

if foo
1
end

if foo; 1; else; 2; end

if foo
1
else
2
end

if foo; 1; elsif bar; 2; else 3; end

include Foo

include Foo
if true; end

extend Foo

extend Foo
if true; end

extend self

unless foo; 1; end

unless foo; 1; else; 2; end

while true; end;

while true; 1; end;

until true; end;

until true; 1; end;

1 if 3

1 unless 3

foo if 3

foo unless 3

a = 1; a += 10 if a += 20

puts a if true

break

break;

break 1

break 1, 2

break {1, 2}

break {1 => 2}

break 1 if true

break if true

next

next;

next 1

next 1, 2

next {1, 2}

next {1 => 2}

next 1 if true

next if true

return

return;

return 1

return 1, 2

return {1, 2}

return {1 => 2}

return 1 if true

return if true

yield

yield;

yield 1

yield 1 if true

yield if true

begin; 1; end;

begin; 1; 2; 3; end;

require "foo"

require "foo"; [1]

()

(1; 2; 3)

if (
true
)
1
end

1 if /x/

1 rescue 2 if 3

1 ensure 2 if 3

yield foo do
end

return do
end

1 while 3

1 until 3

if 1 == 1 a; end

unless 1 == 1 a; end

while 1 == 1 a; end

case 1 == 1 a; when 2; end

case 1 == 1; when 2 a; end

require 1