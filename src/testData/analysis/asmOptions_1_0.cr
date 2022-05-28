# LANGUAGE_LEVEL: 1.0

asm("bl trap" :::: "volatile")
asm("bl trap" :::: "alignstack")
asm("bl trap" :::: "intel")
asm("bl trap" :::: <error descr="Unknown asm option">"unwind"</error>)
asm("bl trap" :::: <error descr="Unknown asm option">"foo"</error>)