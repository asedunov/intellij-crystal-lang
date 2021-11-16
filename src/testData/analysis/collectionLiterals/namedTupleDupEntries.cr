x = {abc: 1, def: 2}

x = {<error descr="Duplicated named tuple entry name: abc">abc</error>: 1, <error descr="Duplicated named tuple entry name: abc">abc</error>: 2}

x = {<error descr="Duplicated named tuple entry name: abc">"abc"</error>: 1, <error descr="Duplicated named tuple entry name: abc">"abc"</error>: 2}

x = {<error descr="Duplicated named tuple entry name: abc
def">"abc\ndef"</error>: 1, <error descr="Duplicated named tuple entry name: abc
def">"abc\x0Adef"</error>: 2}