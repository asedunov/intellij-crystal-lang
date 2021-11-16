f.[]= <error descr="Setter method '[]=' cannot be called with a block">do |a| end</error>

f.[]= <error descr="Setter method '[]=' cannot be called with a block">{ |bar| }</error>

f.[] do |a| end

f.[] { |bar| }

f do |a| end

f { |bar| }