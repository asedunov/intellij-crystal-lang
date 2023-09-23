foo { |(a, (b, (c, d)))| }


foo { |(a, *b, c)| }


foo { |x, x| }


foo { |x, (x)| }


foo { |(x, x)| }


foo { |a, (b, c), (d, e)| a; b; c; d; e }


foo { |(_, c)| c }


foo { |(_, c, )| c }