class <lineMarker descr="<html><body>Is subclassed by<br><br/><code>B</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">A</lineMarker>
  macro foo(a)

  end
end

module <lineMarker descr="<html><body>Is included by<br><br/><code>B</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">M</lineMarker>
  macro foo(a)

  end
end

module <lineMarker descr="<html><body>Is included by<br><br/><code>B</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">N</lineMarker>
  macro <lineMarker descr="<html><body>Is overridden by<br/>macro <code>foo</code> in <code>B.class</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+Alt+B</font></body></html>">foo</lineMarker>(a)

  end
end

class B < A
  include M
  include N

  macro <lineMarker descr="<html><body>Overrides<br/>macro <code>foo</code> in <code>N:Module</code><br/></p><p style='margin-top:8px;'><font size='2'> or press Ctrl+U</font></body></html>">foo</lineMarker>(a)

  end
end