@[<error descr="Missing link arguments">Link</error>]
lib L
end

@[<error descr="Missing link arguments">Link</error>()]
lib L
end

@[Link(<error descr="'lib' argument must be a String">1</error>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <error descr="'ldflags' link argument must be a String">2</error>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <error descr="'static' link argument must be a Bool">1</error>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <warning descr="Specifying static linking for individual libraries is deprecated"><warning descr="Using non-named arguments for Link annotations is deprecated">true</warning></warning>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <warning descr="Specifying static linking for individual libraries is deprecated"><warning descr="Using non-named arguments for Link annotations is deprecated">true</warning></warning>, <error descr="'framework' link argument must be a String">1</error>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <warning descr="Specifying static linking for individual libraries is deprecated"><warning descr="Using non-named arguments for Link annotations is deprecated">true</warning></warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"baz"</warning>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <warning descr="Specifying static linking for individual libraries is deprecated"><warning descr="Using non-named arguments for Link annotations is deprecated">true</warning></warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"baz"</warning>, <error descr="Unknown 'link' argument">1</error>)]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"foo"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"bar"</warning>, <warning descr="Specifying static linking for individual libraries is deprecated"><warning descr="Using non-named arguments for Link annotations is deprecated">true</warning></warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"baz"</warning>, <error descr="Unknown 'link' argument">1</error>, <error descr="Unknown 'link' argument">2</error>)]
lib L
end

@[Link(framework: <error descr="'framework' argument must be a String">1</error>, lib: <error descr="'lib' argument must be a String">2</error>, <warning descr="Specifying static linking for individual libraries is deprecated">static: <error descr="'static' argument must be a Bool">3</error></warning>, ldflags: <error descr="'ldflags' argument must be a String">4</error>, pkg_config:<error descr="'pkg_config' argument must be a String">5</error>, <error descr="Unknown link argument">foo</error>:6)]
lib L
end

@[Link(framework: "foo", lib: "bar", <warning descr="Specifying static linking for individual libraries is deprecated">static: false</warning>, ldflags: "baz", pkg_config: "qux")]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"a"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"b"</warning>, framework: "foo")]
lib L
end

@[Link(<warning descr="Using non-named arguments for Link annotations is deprecated">"a"</warning>, <warning descr="Using non-named arguments for Link annotations is deprecated">"b"</warning>, <error descr="'lib' link argument is already specified">lib: "foo"</error>)]
lib L
end