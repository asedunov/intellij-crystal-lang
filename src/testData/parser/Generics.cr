Foo(T)

Foo(T | U)

Foo(Bar(T | U))

Foo(T?)

Foo(1)

Foo(T, 1)

Foo(T, U, 1)

Foo(T, 1, U)

Foo(typeof(1))

Foo(typeof(1), typeof(2))

Foo({X, Y})

Foo({X, Y,})

Foo({*X, *{Y}})

Foo({->})

Foo({String, ->})

Foo({String, ->, ->})

Foo(x: U)

Foo(x: U, y: V)

Foo(X: U, Y: V)

Foo(T, x: U)

Foo(x: T y: U)

Foo("foo bar": U)

Foo("foo": U, "bar": V)

Foo({x: X})

Foo({x: X, y: Y})

Foo({X: X, Y: Y})

Foo(T, {x: X})

Foo({x: X, typeof: Y})

Foo({x: X, x: Y})

Foo({"foo bar": X})

Foo({"foo": X, "bar": Y})

Foo(*T)

Foo(X, sizeof(Int32))

Foo(X, instance_sizeof(Int32))

Foo(X, offsetof(Foo, @a))

Foo(
T
)

Foo(
T,
U,
)

Foo(
x:
T,
y:
U,
)

Foo()

Foo(Bar())

Foo(
)

Foo()?

a : Foo()*

a : Foo()[12]

Set() {1, 2, 3}