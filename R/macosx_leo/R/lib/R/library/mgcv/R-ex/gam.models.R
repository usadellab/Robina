### Name: gam.models
### Title: Specifying generalized additive models
### Aliases: gam.models
### Keywords: models regression

### ** Examples

set.seed(10)
n<-400
sig2<-4
x0 <- runif(n, 0, 1)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
pi <- asin(1) * 2
f1 <- 2 * sin(pi * x2)
f2 <-  exp(2 * x2) - 3.75887
f3 <-  0.2 * x2^11 * (10 * (1 - x2))^6 + 
       10 * (10 * x2)^3 * (1 - x2)^10 - 1.396
e <- rnorm(n, 0, sqrt(abs(sig2)))
# A continuous `by' variable example.... 
y <- f3*x1 + e
b<-gam(y~x1-1+s(x2,by=x1))
plot(b,pages=1)
summary(b)
# A dummy `by' variable example (with a spurious covariate x0)
fac<-as.factor(c(rep(1,100),rep(2,100),rep(3,200)))
fac.1<-as.numeric(fac==1);fac.2<-as.numeric(fac==2);
fac.3<-as.numeric(fac==3)
y<-f1*fac.1+f2*fac.2+f3*fac.3+ e
b<-gam(y~fac+s(x2,by=fac.1)+s(x2,by=fac.2)+s(x2,by=fac.3)+s(x0))
plot(b,pages=1)
summary(b)



