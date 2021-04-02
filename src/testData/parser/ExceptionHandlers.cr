begin; rescue; end

begin; 1; rescue; 2; end

begin; 1; ensure; 2; end

begin
1
ensure
2
end

begin; 1; rescue Foo; 2; end

begin; 1; rescue ::Foo; 2; end

begin; 1; rescue Foo | Bar; 2; end

begin; 1; rescue ::Foo | ::Bar; 2; end

begin; 1; rescue ex : Foo | Bar; 2; end

begin; 1; rescue ex : ::Foo | ::Bar; 2; end

begin; 1; rescue ex; 2; end

begin; 1; rescue; 2; else; 3; end

begin; 1; rescue ex; 2; end; ex

def foo(); 1; rescue; 2; end

1.tap do; 1; rescue; 2; end

-> do; 1; rescue; 2; end

1.tap do |x|; 1; rescue; x; end

1 rescue 2

x = 1 rescue 2

x = 1 ensure 2

a = 1; a rescue a

a = 1; yield a rescue a

1 ensure 2

1 rescue 2

foo ensure 2

foo rescue 2

a = 1; a ensure a

a = 1; yield a ensure a

def foo
rescue
end

def foo
ensure
end