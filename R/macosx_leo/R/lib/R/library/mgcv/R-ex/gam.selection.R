### Name: gam.selection
### Title: Generalized Additive Model Selection
### Aliases: gam.selection
### Keywords: models regression

### ** Examples

## an example of GCV based model selection
library(mgcv)
set.seed(0) 
n<-400;sig<-2
x0 <- runif(n, 0, 1);x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1);x3 <- runif(n, 0, 1)
x4 <- runif(n, 0, 1);x5 <- runif(n, 0, 1)
f <- 2 * sin(pi * x0)
f <- f + exp(2 * x1) - 3.75887
f <- f+0.2*x2^11*(10*(1-x2))^6+10*(10*x2)^3*(1-x2)^10-1.396
e <- rnorm(n, 0, sig)
y <- f + e
## Note the increased gamma parameter below to favour
## slightly smoother models (See Kim & Gu, 2004, JRSSB)...
b<-gam(y~s(x0,bs="ts")+s(x1,bs="ts")+s(x2,bs="ts")+
   s(x3,bs="ts")+s(x4,bs="ts")+s(x5,bs="ts"),gamma=1.4)
summary(b)
plot(b,pages=1)




