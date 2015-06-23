### Name: shorth
### Title: A location estimator based on the shorth
### Aliases: shorth
### Keywords: arith

### ** Examples

 
  x = c(rnorm(500), runif(500) * 10)
  methods = c("mean", "median", "shorth", "half.range.mode")
  ests = sapply(methods, function(m) get(m)(x))

  if(interactive()) {
    colors = 1:4
    hist(x, 40, col="orange")
    abline(v=ests, col=colors, lwd=3, lty=1:2)
    legend(5, 100, names(ests), col=colors, lwd=3, lty=1:2) 
  }



