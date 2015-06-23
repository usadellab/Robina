### Name: magic
### Title: Stable Multiple Smoothing Parameter Estimation by GCV or UBRE,
###   with optional fixed penalty
### Aliases: magic
### Keywords: models smooth regression

### ** Examples

## Use `magic' for a standard additive model fit ... 
   library(mgcv)
   set.seed(1);n <- 400;sig2 <- 4
   x0 <- runif(n, 0, 1);x1 <- runif(n, 0, 1)
   x2 <- runif(n, 0, 1);x3 <- runif(n, 0, 1)
   f <- 2 * sin(pi * x0)
   f <- f + exp(2 * x1) - 3.75887
   f <- f+0.2*x2^11*(10*(1-x2))^6+10*(10*x2)^3*(1-x2)^10
   e <- rnorm(n, 0, sqrt(sig2))
   y <- f + e
## set up additive model
   G <- gam(y~s(x0)+s(x1)+s(x2)+s(x3),fit=FALSE)
## fit using magic
   mgfit <- magic(G$y,G$X,G$sp,G$S,G$off,G$rank,C=G$C)
## and fit using gam as consistency check
   b <- gam(G=G)
   mgfit$sp;b$sp  # compare smoothing parameter estimates
   edf <- magic.post.proc(G$X,mgfit,G$w)$edf  # extract e.d.f. per parameter
## get termwise e.d.f.s
   twedf <- 0;for (i in 1:4) twedf[i] <- sum(edf[((i-1)*10+1):(i*10)])
   twedf;b$edf  # compare

## Now a correlated data example ... 
    library(nlme)
## simulate truth
    set.seed(1);n<-400;sig<-2
    x <- 0:(n-1)/(n-1)
    f <- 0.2*x^11*(10*(1-x))^6+10*(10*x)^3*(1-x)^10-1.396
## produce scaled covariance matrix for AR1 errors...
    V <- corMatrix(Initialize(corAR1(.6),data.frame(x=x)))
    Cv <- chol(V)  # t(Cv)
## Simulate AR1 errors ...
    e <- t(Cv)%*%rnorm(n,0,sig) # so cov(e) = V * sig^2
## Observe truth + AR1 errors
    y <- f + e 
## GAM ignoring correlation
    par(mfrow=c(1,2))
    b <- gam(y~s(x,k=20))
    plot(b);lines(x,f-mean(f),col=2);title("Ignoring correlation")
## Fit smooth, taking account of *known* correlation...
    w <- solve(t(Cv)) # V^{-1} = w'w
    ## Use `gam' to set up model for fitting...
    G <- gam(y~s(x,k=20),fit=FALSE)
    ## fit using magic, with weight *matrix*
    mgfit <- magic(G$y,G$X,G$sp,G$S,G$off,G$rank,C=G$C,w=w)
## Modify previous gam object using new fit, for plotting...    
    mg.stuff <- magic.post.proc(G$X,mgfit,w)
    b$edf <- mg.stuff$edf;b$Vp <- mg.stuff$Vb
    b$coefficients <- mgfit$b 
    plot(b);lines(x,f-mean(f),col=2);title("Known correlation")



