### Name: loessFit
### Title: Fast Simple Loess
### Aliases: loessFit
### Keywords: models

### ** Examples

y <- rnorm(1000)
x <- rnorm(1000)
w <- rep(1,1000)
# The following are equivalent apart from execution time
# and interpolation inaccuracies
system.time(fit <- loessFit(y,x)$fitted)
system.time(fit <- loessFit(y,x,w)$fitted)
system.time(fit <- fitted(loess(y~x,weights=w,span=0.3,family="symmetric",iterations=4)))
# The same but with sorted x-values
system.time(fit <- lowess(x,y,f=0.3)$y)



