# LANGUAGE_LEVEL: 1.3

lib MyLib
  $x : Int32
end

<error descr="Global variables are not supported, use class variables instead">$x</error>

$~

$?