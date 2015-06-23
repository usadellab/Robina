### Name: smoothScatter
### Title: Scatterplots with smoothed densities color representation
### Aliases: smoothScatter densCols
### Keywords: hplot

### ** Examples

if(interactive()) {
  x1  <- matrix(rnorm(1e4), ncol=2)
  x2  <- matrix(rnorm(1e4, mean=3, sd=1.5), ncol=2)
  x   <- rbind(x1,x2)

  oldpar <- par(mfrow=c(2,2))
  smoothScatter(x, nrpoints=0)
  smoothScatter(x)
  smoothScatter(x,nrpoints=Inf,colramp=colorRampPalette(RColorBrewer::brewer.pal(9, "YlOrRd")),bandwidth=1)  

  colors  <- densCols(x)
  plot(x, col=colors, pch=20)

  ## use with pairs:
  par(mfrow=c(1,1))
  y <- matrix(rnorm(40000), ncol=4) + 3*rnorm(10000)
  y[, c(2,4)] <- (-y[, c(2,4)])
  pairs(y, panel=function(...) {par(new=TRUE);smoothScatter(..., nrpoints=0)})

  par(oldpar)
}



