### Name: findLargest
### Title: Find the Entrez Gene ID corresponding to the largest statistic
### Aliases: findLargest
### Keywords: manip

### ** Examples

  library("hgu95av2.db")
  set.seed(124)
  gN <- sample(ls(hgu95av2ENTREZID), 200)
  stats <- rnorm(200)
  findLargest(gN, stats, "hgu95av2")




