macro <lineMarker descr="<html><body>Is overridden by<br/>macro <code>foo</code></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">foo</lineMarker>(a, b, c, **d)

end

macro foo(a, b, c)

end

macro <lineMarker descr="<html><body>Overrides<br/>macro <code>foo</code></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+U</font></body></html>">foo</lineMarker>(x, y, z, **u)

end