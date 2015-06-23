### Name: gam.check
### Title: Some diagnostics for a fitted gam model
### Aliases: gam.check
### Keywords: models smooth regression

### ** Examples

library(mgcv)
set.seed(0)
n<-200
sig<-2
x0 <- runif(n, 0, 1)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
x3 <- runif(n, 0, 1)
y <- 2 * sin(pi * x0)
y <- y + exp(2 * x1) - 3.75887
y <- y+0.2*x2^11*(10*(1-x2))^6+10*(10*x2)^3*(1-x2)^10-1.396
e <- rnorm(n, 0, sig)
y <- y + e
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3))
plot(b,pages=1)
gam.check(b)



