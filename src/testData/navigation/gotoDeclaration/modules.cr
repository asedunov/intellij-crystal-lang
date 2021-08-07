# SEARCH_TEXT: foo
# RESULT: module Foo
# RESULT: module Foo in X
# RESULT: module FooBar
# RESULT: module FooBar in X

module Foo
end

module FooBar
end

module Bar
end

module X
  module Foo
  end
  module FooBar
  end
  module Bar
  end
end