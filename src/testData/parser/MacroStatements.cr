{% a = 1 %}

{%
a = 1
%}

{% a = 1 if 2 %}

{% if 1; 2; end %}

{%
if 1; 2; end
%}

{% unless 1; 2; end %}

{% unless 1; 2; else 3; end %}

{%
1
2
3
%}

{% unless 1; 2; elsif 3; 4; end %}

lib LibC; {% if 1 %}2{% end %}; end

lib LibC; struct Foo; {% if 1 %}2{% end %}; end; end

{% unless 1 %} 2 {% elsif 3 %} 3 {% end %}

macro foo;bar{% for x in y %}body{% end %}baz;end

macro foo;bar{% for x, y in z %}body{% end %}baz;end

macro foo;bar{% if x %}body{% end %}baz;end

macro foo;bar{% if x %}body{% else %}body2{%end%}baz;end

macro foo;bar{% if x %}body{% elsif y %}body2{%end%}baz;end

macro foo;bar{% if x %}body{% elsif y %}body2{% else %}body3{%end%}baz;end

macro foo;bar{% unless x %}body{% end %}baz;end

macro foo;bar{% for x in y %}\
   body{% end %}baz;end

macro foo;bar{% for x in y %}\
   body{% end %}\   baz;end

macro foo;{% verbatim do %}1{% foo %}2{% end %};end

macro foo
{%
if 1
2
else
3
end
%}end

macro foo;bar{% begin %}body{% end %}baz;end

{% for x in y %}body{% end %}

{% if x %}body{% end %}

{% begin %}{% if true %}if true{% end %}
{% if true %}end{% end %}{% end %}

{% if true %}
{% end %}
{% if true %}
{% end %}

enum Foo; {% if 1 %}2{% end %}; end

{% for _, x, _ in y %}body{% end %}