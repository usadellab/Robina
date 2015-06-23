### Name: ttest
### Title: A filter function for a t.test
### Aliases: ttest
### Keywords: manip

### ** Examples

  dat <- c(rep(1,5),rep(2,5))
  set.seed(5)
  y <- rnorm(10)
  af <- ttest(dat, .01)
  af(y)
  af2 <- ttest(5, .01)
  af2(y)
  y[8] <- NA
  af(y)
  af2(y)
  y[1:5] <- y[1:5]+10
  af(y)



