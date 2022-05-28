# LANGUAGE_LEVEL: 1.2

asm("bl trap" :::: "volatile")
asm("bl trap" :::: "alignstack")
asm("bl trap" :::: "intel")
asm("bl trap" :::: "unwind")
asm("bl trap" :::: <error descr="Unknown asm option">"foo"</error>)