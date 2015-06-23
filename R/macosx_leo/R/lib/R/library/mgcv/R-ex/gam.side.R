### Name: gam.side
### Title: Identifiability side conditions for a GAM
### Aliases: gam.side
### Keywords: models regression

### ** Examples

set.seed(0)
n<-400
sig2<-4
x0 <- runif(n, 0, 1)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
pi <- asin(1) * 2
f <- 2 * sin(pi * x0)
f <- f + exp(2 * x1) - 3.75887
f <- f + 0.2 * x2^11 * (10 * (1 - x2))^6 + 10 * (10 * x2)^3 * (1 - x2)^10 - 1.396
e <- rnorm(n, 0, sqrt(abs(sig2)))
y <- f + e
b<-gam(y~s(x0)+s(x1)+s(x0,x1)+s(x2))
plot(b,pages=1)
test1<-function(x,z,sx=0.3,sz=0.4)  
{ (pi**sx*sz)*(1.2*exp(-(x-0.2)^2/sx^2-(z-0.3)^2/sz^2)+
  0.8*exp(-(x-0.7)^2/sx^2-(z-0.8)^2/sz^2))
}
n<-500
old.par<-par(mfrow=c(2,2))
x<-runif(n);z<-runif(n);
y<-test1(x,z)+rnorm(n)*0.1
## a fully nested tensor product example
b<-gam(y~s(x,bs="cr",k=6)+s(z,bs="cr",k=6)+te(x,z,k=6))
plot(b)
par(old.par)
rm(list=c("f","x0","x1","x2","x","z","y","b","test1","n","sig2","pi","e"))



