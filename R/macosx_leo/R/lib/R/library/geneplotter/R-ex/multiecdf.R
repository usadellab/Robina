### Name: multiecdf
### Title: Multiple empirical cumulative distribution functions (ecdf) and
###   densities
### Aliases: multiecdf multiecdf.default multiecdf.formula multiecdf.matrix
###   multidensity multidensity.default multidensity.formula
###   multidensity.matrix
### Keywords: hplot

### ** Examples

  f = 1 + (runif(1000)>0.5)
  x = rnorm(length(f), mean=f, sd=f)
  
  multiecdf(x~f)
  multidensity(x~f)



