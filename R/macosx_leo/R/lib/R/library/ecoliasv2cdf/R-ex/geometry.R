### Name: i2xy
### Title: Convert (x,y)-coordinates to single-number indices and back.
### Aliases: i2xy xy2i
### Keywords: datasets

### ** Examples

  xy2i(5,5)
  i     = 1:(544*544)
  coord = i2xy(i)
  j     = xy2i(coord[, "x"], coord[, "y"])
  stopifnot(all(i==j))
  range(coord[, "x"])
  range(coord[, "y"])



