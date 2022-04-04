class A
end
def foo
end

require "foo"
require "bar"

class A
end
require "foo"
class B
end

require "foo"
class A
end
require "bar"
class B
end
require "baz"