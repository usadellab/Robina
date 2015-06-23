### Name: gam.neg.bin
### Title: GAMs with the negative binomial distribution
### Aliases: gam.neg.bin mgcv.find.theta mgcv.get.scale
### Keywords: models regression

### ** Examples

library(MASS) # required for negative binomial families
set.seed(3)
n<-400
x0 <- runif(n, 0, 1)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
x3 <- runif(n, 0, 1)
pi <- asin(1) * 2
f <- 2 * sin(pi * x0)
f <- f + exp(2 * x1) - 3.75887
f <- f + 0.2 * x2^11 * (10 * (1 - x2))^6 + 10 * (10 * x2)^3 * (1 - x2)^10 - 1.396
g<-exp(f/5)
# negative binomial data  
y<-rnbinom(g,size=3,mu=g)
# unknown theta ...
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=negative.binomial(1))
plot(b,pages=1)
print(b)
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=neg.bin(1)) # unknown theta
plot(b,pages=1)
print(b)
# known theta example ...
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=negative.binomial(3),scale=1)
plot(b,pages=1)
print(b)
# Now use "sqrt" link available in negative.binomial (but not neg.bin)
set.seed(1)
f<-f-min(f);g<-f^2
y<-rnbinom(g,size=3,mu=g)
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=negative.binomial(1,link="sqrt")) 
plot(b,pages=1)
print(b)



