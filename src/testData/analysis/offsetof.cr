offsetof(X, 123)

offsetof(X, 123_i32)

offsetof(X, <error descr="Integer offset must have Int32 type">123_i8</error>)

offsetof(X, <error descr="Integer offset must have Int32 type">0x80000000</error>)