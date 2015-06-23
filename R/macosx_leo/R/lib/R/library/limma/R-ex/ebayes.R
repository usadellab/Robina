### Name: ebayes
### Title: Empirical Bayes Statistics for Differential Expression
### Aliases: ebayes eBayes
### Keywords: htest

### ** Examples

#  See also lmFit examples

#  Simulate gene expression data,
#  6 microarrays and 100 genes with one gene differentially expressed
set.seed(2004); invisible(runif(100))
M <- matrix(rnorm(100*6,sd=0.3),100,6)
M[1,] <- M[1,] + 1
fit <- lmFit(M)

#  Ordinary t-statistic
par(mfrow=c(1,2))
ordinary.t <- fit$coef / fit$stdev.unscaled / fit$sigma
qqt(ordinary.t,df=fit$df.residual,main="Ordinary t")
abline(0,1)

#  Moderated t-statistic
eb <- eBayes(fit)
qqt(eb$t,df=eb$df.prior+eb$df.residual,main="Moderated t")
abline(0,1)
#  Points off the line may be differentially expressed
par(mfrow=c(1,1))



