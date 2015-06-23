### Name: RPadvance
### Title: Advanced Rank Product Analysis of Microarray
### Aliases: RPadvance
### Keywords: htest

### ** Examples

      # Load the data of Golub et al. (1999). data(golub) 
      # contains a 3051x38 gene expression
      # matrix called golub, a vector of length called golub.cl 
      # that consists of the 38 class labels,
      # and a matrix called golub.gnames whose third column 
      # contains the gene names.
      data(golub)

      ##For data with single origin
      subset <- c(1:4,28:30)
      origin <- rep(1,7)
      #identify genes 
      RP.out <- RPadvance(golub[,subset],golub.cl[subset],
                           origin,plot=FALSE,rand=123)
      
      #For data from multiple origins
      
      #Load the data arab in the package, which contains 
      # the expression of 22,081 genes
      # of control and treatment group from the experiments 
      #indenpently conducted at two 
      #laboratories.
      data(arab)
      arab.origin #1 1 1 1 1 1 2 2 2 2
      arab.cl #0 0 0 1 1 1 0 0 1 1
      RP.adv.out <- RPadvance(arab,arab.cl,arab.origin,
                    num.perm=100,gene.names=arab.gnames,logged=TRUE,rand=123)

      attributes(RP.adv.out)
      head(RP.adv.out$pfp)
      head(RP.adv.out$RPs)
      head(RP.adv.out$AveFC)




