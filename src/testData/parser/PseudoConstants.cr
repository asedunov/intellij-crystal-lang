__LINE__

__FILE__

__DIR__

__END_LINE__

def foo(x = __LINE__); end

def foo(x = __FILE__); end

def foo(x = __DIR__); end

def foo(x = __END_LINE__); end

def foo(x = __LINE__ + 1); end

def foo(x = __FILE__ + 1); end

def foo(x = __DIR__ + 1); end

def foo(x = __END_LINE__ + 1); end

puts __FILE__

puts __DIR__

puts __LINE__

puts __END_LINE__