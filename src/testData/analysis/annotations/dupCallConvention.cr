@[CallConvention("C")]
lib L
end

@[<error descr="Call convention is already specified">CallConvention</error>("C")]
lib L
end