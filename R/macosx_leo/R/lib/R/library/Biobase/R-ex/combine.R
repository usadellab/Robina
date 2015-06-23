### Name: combine
### Title: Methods for Function combine in Package `Biobase'
### Aliases: combine combine,ANY,missing-method
###   combine,data.frame,data.frame-method combine,matrix,matrix-method
### Keywords: manip

### ** Examples

  x <- data.frame(x=1:5,
          y=factor(letters[1:5], levels=letters[1:8]),
          row.names=letters[1:5])
  y <- data.frame(z=3:7,
          y=factor(letters[3:7], levels=letters[1:8]),
          row.names=letters[3:7])
  combine(x,y)

  w <- data.frame(w=4:8,
         y=factor(letters[4:8], levels=letters[1:8]),
         row.names=letters[4:8])
  combine(w, x, y)

  # y is converted to 'factor' with different levels
  df1 <- data.frame(x=1:5,y=letters[1:5], row.names=letters[1:5])
  df2 <- data.frame(z=3:7,y=letters[3:7], row.names=letters[3:7])
  try(combine(df1, df2)) # fails
  # solution 1: ensure identical levels
  y1 <- factor(letters[1:5], levels=letters[1:7])
  y2 <- factor(letters[3:7], levels=letters[1:7])
  df1 <- data.frame(x=1:5,y=y1, row.names=letters[1:5])
  df2 <- data.frame(z=3:7,y=y2, row.names=letters[3:7])
  combine(df1, df2)
  # solution 2: force column to be 'character'
  df1 <- data.frame(x=1:5,y=I(letters[1:5]), row.names=letters[1:5])
  df2 <- data.frame(z=3:7,y=I(letters[3:7]), row.names=letters[3:7])
  combine(df1, df2)

  m <- matrix(1:20, nrow=5, dimnames=list(LETTERS[1:5], letters[1:4]))
  combine(m[1:3,], m[4:5,])
  combine(m[1:3, 1:3], m[3:5, 3:4]) # overlap



