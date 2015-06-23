### Name: genefilter
### Title: A function to filter genes.
### Aliases: genefilter
### Keywords: manip

### ** Examples

   set.seed(-1)
   f1 <- kOverA(5, 10)
   flist <- filterfun(f1, allNA)
   exprA <- matrix(rnorm(1000, 10), ncol = 10)
   ans <- genefilter(exprA, flist)



