### Name: gapFilter
### Title: A filter to select genes based on there being a gap.
### Aliases: gapFilter
### Keywords: manip

### ** Examples

 set.seed(256)
 x <- c(rnorm(10,100,3), rnorm(10, 100, 10))
 y <- x + c(rep(0,10), rep(100,10))
 tmp <- rbind(x,y) 
 Gfilter <- gapFilter(200, 100, 5)
 ffun <- filterfun(Gfilter)
 genefilter(tmp, ffun)



