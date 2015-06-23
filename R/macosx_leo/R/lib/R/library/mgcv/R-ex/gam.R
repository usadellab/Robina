### Name: gam
### Title: Generalized additive models with integrated smoothness
###   estimation
### Aliases: gam
### Keywords: models smooth regression

### ** Examples

library(mgcv)
set.seed(0) 
n<-400
sig<-2
x0 <- runif(n, 0, 1)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
x3 <- runif(n, 0, 1)
f0 <- function(x) 2 * sin(pi * x)
f1 <- function(x) exp(2 * x)
f2 <- function(x) 0.2*x^11*(10*(1-x))^6+10*(10*x)^3*(1-x)^10
f3 <- function(x) 0*x
f <- f0(x0) + f1(x1) + f2(x2)
e <- rnorm(n, 0, sig)
y <- f + e
b<-gam(y~s(x0)+s(x1)+s(x2)+s(x3))
summary(b)
plot(b,pages=1,residuals=TRUE)
# same fit in two parts .....
G<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),fit=FALSE)
b<-gam(G=G)
# an extra ridge penalty (useful with convergence problems) ....
bp<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),H=diag(0.5,37)) 
print(b);print(bp);rm(bp)
# set the smoothing parameter for the first term, estimate rest ...
bp<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),sp=c(0.01,-1,-1,-1))
plot(bp,pages=1);rm(bp)
# set lower bounds on smoothing parameters ....
bp<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),min.sp=c(0.001,0.01,0,10)) 
print(b);print(bp);rm(bp)

# now a GAM with 3df regression spline term & 2 penalized terms
b0<-gam(y~s(x0,k=4,fx=TRUE,bs="tp")+s(x1,k=12)+s(x2,k=15))
plot(b0,pages=1)
# now fit a 2-d term to x0,x1
b1<-gam(y~s(x0,x1)+s(x2)+s(x3))
par(mfrow=c(2,2))
plot(b1)
par(mfrow=c(1,1))

# now simulate poisson data
g<-exp(f/4)
y<-rpois(rep(1,n),g)
b2<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=poisson)
plot(b2,pages=1)
# repeat fit using performance iteration
gm <- gam.method(gam="perf.magic")
b3<-gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=poisson,method=gm)
plot(b3,pages=1)

# a binary example 
g <- (f-5)/3
g <- binomial()$linkinv(g)
y <- rbinom(g,1,g)
lr.fit <- gam(y~s(x0)+s(x1)+s(x2)+s(x3),family=binomial)
## plot model components with truth overlaid in red
op <- par(mfrow=c(2,2))
for (k in 1:4) {
  plot(lr.fit,residuals=TRUE,select=k)
  xx <- sort(eval(parse(text=paste("x",k-1,sep=""))))
  ff <- eval(parse(text=paste("f",k-1,"(xx)",sep="")))
  lines(xx,(ff-mean(ff))/3,col=2)
}
par(op)
anova(lr.fit)
lr.fit1 <- gam(y~s(x0)+s(x1)+s(x2),family=binomial)
lr.fit2 <- gam(y~s(x1)+s(x2),family=binomial)
AIC(lr.fit,lr.fit1,lr.fit2)

# and a pretty 2-d smoothing example....
test1<-function(x,z,sx=0.3,sz=0.4)  
{ (pi**sx*sz)*(1.2*exp(-(x-0.2)^2/sx^2-(z-0.3)^2/sz^2)+
  0.8*exp(-(x-0.7)^2/sx^2-(z-0.8)^2/sz^2))
}
n<-500
old.par<-par(mfrow=c(2,2))
x<-runif(n);z<-runif(n);
xs<-seq(0,1,length=30);zs<-seq(0,1,length=30)
pr<-data.frame(x=rep(xs,30),z=rep(zs,rep(30,30)))
truth<-matrix(test1(pr$x,pr$z),30,30)
contour(xs,zs,truth)
y<-test1(x,z)+rnorm(n)*0.1
b4<-gam(y~s(x,z))
fit1<-matrix(predict.gam(b4,pr,se=FALSE),30,30)
contour(xs,zs,fit1)
persp(xs,zs,truth)
vis.gam(b4)
par(old.par)
# very large dataset example with user defined knots
n<-10000
x<-runif(n);z<-runif(n);
y<-test1(x,z)+rnorm(n)
ind<-sample(1:n,1000,replace=FALSE)
b5<-gam(y~s(x,z,k=50),knots=list(x=x[ind],z=z[ind]))
vis.gam(b5)
# and a pure "knot based" spline of the same data
b6<-gam(y~s(x,z,k=100),knots=list(x= rep((1:10-0.5)/10,10),
        z=rep((1:10-0.5)/10,rep(10,10))))
vis.gam(b6,color="heat")
# varying the default large dataset behaviour via `xt'
b7 <- gam(y~s(x,z,k=50,xt=list(max.knots=1000,seed=2)))
vis.gam(b7)




