module MyModule
  module MyInnerModule
  end

  class MyInnerClass
  end

  struct MyInnerStruct
  end

  enum MyInnerEnum
  end
end

module Foo::MyContainedModule
end

class MyClass
  module MyInnerModule
  end

  class MyInnerClass
  end

  struct MyInnerStruct
  end

  enum MyInnerEnum
  end
end

class Foo::MyContainedClass
end

struct MyStruct
  module MyInnerModule
  end

  class MyInnerClass
  end

  struct MyInnerStruct
  end

  enum MyInnerEnum
  end
end

struct Foo::MyContainedStruct
end

enum MyEnum
end

enum Foo::MyContainedEnum
end

lib MyLib
  enum MyInnerEnum
  end
end