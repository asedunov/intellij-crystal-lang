lib LibC; {{ 1 }}; end

lib LibC; struct Foo; {{ 1 }}; end; end

puts {{1}}

puts {{
1
}}

puts {{*1}}

puts {{**1}}

{{a = 1 if 2}}

{{ 1 // 2 }}

{{ //.options }}

{{ foo }}

enum Foo; {{1}}; end

{{ {{ 1 }} }}

{{ {% 1 %} }}

def foo;{{@type}};end

macro foo;%var;end

macro foo;%var{1, x} = hello;end

macro !;end