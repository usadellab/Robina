### Name: venn
### Title: Venn Diagrams
### Aliases: vennCounts vennDiagram
### Keywords: htest

### ** Examples

Y <- matrix(rnorm(100*6),100,6)
Y[1:10,3:4] <- Y[1:10,3:4]+3
Y[1:20,5:6] <- Y[1:20,5:6]+3
design <- cbind(1,c(0,0,1,1,0,0),c(0,0,0,0,1,1))
fit <- eBayes(lmFit(Y,design))
results <- decideTests(fit)
a <- vennCounts(results)
print(a)
vennDiagram(a)
vennDiagram(results,include=c("up","down"),counts.col=c("red","green"))



