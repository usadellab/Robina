### Name: convest
### Title: Estimate Proportion of True Null Hypotheses
### Aliases: convest
### Keywords: htest

### ** Examples

# First simulate data, use no.genes genes and no.ind individuals,
# with given value of pi0. Draw from normal distribution with mean=0
# (true null) and mean=mean.diff (false null).

no.genes <- 1000
no.ind <- 20
pi0 <- 0.9
mean.diff <- 1
n1 <- round(pi0*no.ind*no.genes)
n2 <- round((1-pi0)*no.ind*no.genes)
x <- matrix(c(rnorm(n1,mean=0),rnorm(n2,mean=mean.diff)),ncol=no.ind,byrow=TRUE)

# calculate p-values using your favorite method, e.g.
pvals <- ebayes(lm.series(x))$p.value

# run the convex decreasing density estimator to estimate pi0
convest(pvals,niter=100,doplot=interactive())



