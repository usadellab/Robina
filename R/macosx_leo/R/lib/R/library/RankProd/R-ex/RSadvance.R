### Name: RSadvance
### Title: Advanced Rank Sum Analysis of Microarray
### Aliases: RSadvance
### Keywords: htest

### ** Examples

      
      #Suppose we want to check the consistence of the data 
      #sets generated in two different 
      #labs. For example, we would look for genes that were \
      # measured to be up-regulated in 
      #class 2 at lab 1, but down-regulated in class 2 at lab 2.\
       data(arab)
      arab.cl2 <- arab.cl

      arab.cl2[arab.cl==0 &arab.origin==2] <- 1

      arab.cl2[arab.cl==1 &arab.origin==2] <- 0

      arab.cl2
  ##[1] 0 0 0 1 1 1 1 1 0 0

      #look for genes differentially expressed
      #between hypothetical class 1 and 2
      arab.sub=arab[1:500,] ##using subset for fast computation
      arab.gnames.sub=arab.gnames[1:500]
      Rsum.adv.out <- RSadvance(arab.sub,arab.cl2,arab.origin,
                          num.perm=100,
logged=TRUE,
                          gene.names=arab.gnames.sub,rand=123)

      attributes(Rsum.adv.out)
      



