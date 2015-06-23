### Name: plotGene
### Title: Graphical Display of The expression levels
### Aliases: plotGene
### Keywords: htest

### ** Examples

     
      # Load the data of Golub et al. (1999). data(golub) 
      #contains a 3051x38 gene expression
      # matrix called golub, a vector of length called golub.cl 
      #that consists of the 38 class labels,
      # and a matrix called golub.gnames whose third column contains the gene names.
      data(golub)
 
      #use a subset of data as example, apply the rank product method
      subset <- c(1:4,28:30)
      #Setting rand=123, to make the results reproducible,

      #identify genes that are up-regulated in class 2 
      #(class label =1)
      RP.out <- RP(golub[,subset],golub.cl[subset], rand=123)
      
      #plot the results
      plotRP(RP.out,cutoff=0.05)
      



