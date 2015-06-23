### Name: filterfun
### Title: Creates a first FALSE exiting function from the list of filter
###   functions it is given.
### Aliases: filterfun
### Keywords: manip

### ** Examples

 set.seed(333)
 x <- matrix(rnorm(100,2,1),nc=10)
 cvfun <- cv(.5,2.5)
 ffun <- filterfun(cvfun)
 which <- genefilter(x, ffun)



