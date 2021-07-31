lib MyLib
    $myVar = x : Int32
    <error descr="External variables must start with lowercase">$MyVar</error> = x : Int32
end