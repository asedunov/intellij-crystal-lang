def foo(x : Int32 = __END_LINE__)
end

puts(__FILE__)

puts(__LINE__)

puts(__DIR__)

puts(<error descr="__END_LINE__ can only be used in default argument value">__END_LINE__</error>)