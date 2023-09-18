def foo(x : <error descr="Splat type is not allowed here">*</error>Int32); end

def foo(x : (<error descr="Splat type is not allowed here">*</error>Int32)); end

def foo(x : {*Int32, String}); end

def foo(x : {a: <error descr="Splat type is not allowed here">*</error>Int32, b: String}); end

def foo(x : Foo(*Int32, String)); end

def foo(x : Foo(a: <error descr="Splat type is not allowed here">*</error>Int32, b: String)); end