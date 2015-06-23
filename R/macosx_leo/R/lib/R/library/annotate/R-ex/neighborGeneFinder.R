### Name: neighborGeneFinder
### Title: A widget for locating genes neighboring a target gene
### Aliases: neighborGeneFinder
### Keywords: interface

### ** Examples

  if(interactive()){
     require("annotate", character.only = TRUE) ||
     stop("Package annotate is not availble")
     geneData <- cbind(paste("100", 1:16, "_at", sep = ""), c(1, 50,
                       10044, 51, 71, 51371, 81, 51426, 188, 293, 360,
                       364, 375, 387, 513, 10572))
     colnames(geneData) <- c("Probe", "locuslink")
     neighborGeneFinder(geneData, "locuslink", "human")
  }
  



