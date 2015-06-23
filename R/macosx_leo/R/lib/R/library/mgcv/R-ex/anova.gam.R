### Name: anova.gam
### Title: Hypothesis tests related to GAM fits
### Aliases: anova.gam print.anova.gam
### Keywords: models smooth regression

### ** Examples

library(mgcv)
set.seed(0)
n<-200
sig<-2
x0 <- rep(1:4,50)
x1 <- runif(n, 0, 1)
x2 <- runif(n, 0, 1)
x3 <- runif(n, 0, 1)
y <- 2 * x0
y <- y + exp(2 * x1)
y <- y + 0.2 * x2^11 * (10 * (1 - x2))^6 + 10 * (10 * x2)^3 * (1 - x2)^10
e <- rnorm(n, 0, sig)
y <- y + e
x0<-as.factor(x0)
b<-gam(y~x0+s(x1)+s(x2)+s(x3))
anova(b)
b1<-gam(y~x0+s(x1)+s(x2))
anova(b,b1,test="F")



