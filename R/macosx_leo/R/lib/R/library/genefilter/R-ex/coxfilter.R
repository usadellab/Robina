### Name: coxfilter
### Title: A filter function for univariate Cox regression.
### Aliases: coxfilter
### Keywords: manip

### ** Examples

   set.seed(-5)
   sfun <- coxfilter(rexp(10), ifelse(runif(10) < .7, 1, 0), .05)
   ffun <- filterfun(sfun)
   dat <- matrix(rnorm(1000), ncol=10)
   out <- genefilter(dat, ffun)



