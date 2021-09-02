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

{{ {{1}} }}

{% {{1}} %}

{% if true %} %foo{ {{1}} } {% end %}

{% if {{true}} %} 1 {% elsif {{true}} %} 2 {% end %}

{% unless {{true}} %} 1 {% end %}

{% for x in {{y}} %} 1 {% end %}

{{ {% 1 %} }}

{% {% 1 %} %}

{% if true %} %foo{ {% 1 %} } {% end %}

{% if {% true %} %} 1 {% elsif {% true %} %} 2 {% end %}

{% unless {% true %} %} 1 {% end %}

{% for x in {% y %} %} 1 {% end %}

def foo;{{@type}};end

macro foo;%var;end

macro foo;%var{1, x} = hello;end

macro !;end

{{ foo.nil? }}

{{ foo &.nil? }}

{{ foo.nil?(foo) }}

{{ nil?(foo) }}