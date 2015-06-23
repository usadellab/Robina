### Name: choose.k
### Title: Basis dimension choice for smooths
### Aliases: choose.k
### Keywords: models regression

### ** Examples

## Simulate some data ....
library(mgcv)
set.seed(0) 
n<-400;sig<-2
x0 <- runif(n, 0, 1);x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1);x3 <- runif(n, 0, 1)
f <- 2 * sin(pi * x0)
f <- f + exp(2 * x1) - 3.75887
f <- f+0.2*x2^11*(10*(1-x2))^6+10*(10*x2)^3*(1-x2)^10-1.396
e <- rnorm(n, 0, sig)
y <- f + e
## fit a GAM with quite low `k'
b<-gam(y~s(x0,k=6)+s(x1,k=6)+s(x2,k=6)+s(x3,k=6))
plot(b,pages=1)

## check for residual pattern, removeable by increasing `k'
## typically `k', below, chould be substantially larger than 
## the original, `k' but certainly less than n/2.
## Note use of cheap "cs" shrinkage smoothers, and gamma=1.4
## to reduce chance of overfitting...
rsd <- residuals(b)
gam(rsd~s(x0,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~s(x1,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~s(x2,k=40,bs="cs"),gamma=1.4) ## original `k' too low
gam(rsd~s(x3,k=40,bs="cs"),gamma=1.4) ## fine

## similar example with multi-dimensional smooth
b1 <- gam(y~s(x0)+s(x1,x2,k=15)+s(x3))
rsd <- residuals(b1)
gam(rsd~s(x0,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~s(x1,x2,k=100,bs="ts"),gamma=1.4) ## original `k' too low
gam(rsd~s(x3,k=40,bs="cs"),gamma=1.4) ## fine
 
## and a `te' example
b2 <- gam(y~s(x0)+te(x1,x2,k=4)+s(x3))
rsd <- residuals(b2)
gam(rsd~s(x0,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~te(x1,x2,k=10,bs="cs"),gamma=1.4) ## original `k' too low
gam(rsd~s(x3,k=40,bs="cs"),gamma=1.4) ## fine

## same approach works with other families in the original model
g<-exp(f/4)
y<-rpois(rep(1,n),g)
bp<-gam(y~s(x0,k=6)+s(x1,k=6)+s(x2,k=6)+s(x3,k=6),family=poisson)
rsd <- residuals(bp)
gam(rsd~s(x0,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~s(x1,k=40,bs="cs"),gamma=1.4) ## fine
gam(rsd~s(x2,k=40,bs="cs"),gamma=1.4) ## original `k' too low
gam(rsd~s(x3,k=40,bs="cs"),gamma=1.4) ## fine
 



