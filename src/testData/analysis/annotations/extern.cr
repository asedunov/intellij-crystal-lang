@[Extern]
struct S1

end

@[Extern()]
struct S2

end

<error descr="@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value">@[Extern(1)]</error>
struct S3

end

<error descr="@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value">@[Extern(true)]</error>
struct S4

end

<error descr="@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value">@[Extern(a: 1, union: false)]</error>
struct S3

end

<error descr="@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value">@[Extern(union: 1)]</error>
struct S5

end

@[Extern(union: false)]
struct S6

end

@[Extern(union: true)]
struct S7

end

<error descr="@Extern annotation on non-generic struct must have a single named parameter 'union' with boolean literal as a value">@[Extern(union: false || true)]</error>
struct S8

end