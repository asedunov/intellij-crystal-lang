@[<error descr="CallConvention must have a single positional argument">CallConvention</error>]
lib L1
end

@[CallConvention<error descr="CallConvention must have a single positional argument">()</error>]
lib L2
end

@[CallConvention(<error descr="CallConvention argument must be a string">1</error>)]
lib L3
end

@[CallConvention<error descr="CallConvention must have a single positional argument">(convention: "C")</error>]
lib L4
end

@[CallConvention("C")]
lib L5
end

@[CallConvention("Fast")]
lib L6
end

@[CallConvention("Cold")]
lib L7
end

@[CallConvention("WebKit_JS")]
lib L8
end

@[CallConvention("AnyReg")]
lib L9
end

@[CallConvention("X86_StdCall")]
lib L10
end

@[CallConvention("X86_FastCall")]
lib L11
end

@[CallConvention(<error descr="Invalid call convention">"Foo"</error>)]
lib L12
end