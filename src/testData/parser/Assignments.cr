a = 1

a = b = 2

a, b = 1, 2

a, b = 1

_, _ = 1, 2

a[0], a[1] = 1, 2

a.foo, a.bar = 1, 2

x = 0; a, b = x += 1

a, b = 1, 2 if 3

@a, b = 1, 2

@@a, b = 1, 2

b? = 1

b! = 1

a, B = 1, 2

1 == 2, a = 4

b, 1 == 2, a = 4

a = 1, 2, 3

a = 1, b = 2

a = 1; a &&= 1

a = 1; a ||= 1

a = 1; a[2] &&= 3

a = 1; a[2] ||= 3

x : String, a = 4

r = 1; r.x += 2

A = 1

b.c ||= 1

b.c &&= 1

a = a

x.y=(1).to_s

a = 1; b = 2; a, b += 1, 2

a += 1

self = 1

self += 1

FOO, BAR = 1, 2

self, x = 1, 2

x, self = 1, 2

x++

x--

a = 1; a += 2

a = 1; a.x += 2