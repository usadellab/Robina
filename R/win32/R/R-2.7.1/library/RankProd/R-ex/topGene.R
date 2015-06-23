### Name: topGene
### Title: Output Significant Genes
### Aliases: topGene
### Keywords: htest

### ** Examples


      # Load the data of Golub et al. (1999). data(golub) 
      # contains a 3051x38 gene expression
      # matrix called golub, a vector of length called golub.cl 
      # that consists of the 38 class labels,
      # and a matrix called golub.gnames whose third column 
      # contains the gene names.
      data(golub)

      #use a subset of data as example, apply the rank 
      #product method
      subset <- c(1:4,28:30)
      #Setting rand=123, to make the results reproducible,

      #identify genes 
      RP.out <- RP(golub[,subset],golub.cl[subset],rand=123)  

      #get two lists of differentially expressed genes 
      #by setting FDR (false discivery rate) =0.05

      table=topGene(RP.out,cutoff=0.05,method="pfp",logged=TRUE,logbase=2,
                   gene.names=golub.gnames[,3])
      table$Table1
      table$Table2

      #using pvalue<0.05
      topGene(RP.out,cutoff=0.05,method="pval",logged=TRUE,logbase=2,
                   gene.names=golub.gnames[,3])

      #by selecting top 10 genes

      topGene(RP.out,num.gene=10,gene.names=golub.gnames[,3])




