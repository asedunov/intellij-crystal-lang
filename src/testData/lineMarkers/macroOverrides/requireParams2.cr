macro <lineMarker descr="<html><body>Is overridden by<br/>macro <code>foo</code></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">foo</lineMarker>(*a, x, y = 1, z)

end

macro foo(*a, u, v = 1, w)

end

macro <lineMarker descr="<html><body>Overrides<br/>macro <code>foo</code></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+U</font></body></html>">foo</lineMarker>(*a, x, w = 1, z)

end