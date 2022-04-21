module <lineMarker descr="<html><body>Is included by<br><br/><code>B</code><br/><code>C</code><br/><code>N</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">M</lineMarker>

end

class <lineMarker descr="<html><body>Is subclassed by<br><br/><code>B</code><br/><code>C</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">A</lineMarker>

end

class <lineMarker descr="<html><body>Is subclassed by<br><br/><code>C</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">B</lineMarker> < A
  include M
end

module <lineMarker descr="<html><body>Is included by<br><br/><code>C</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">N</lineMarker>
  include M
end

class C < B
  include N
end