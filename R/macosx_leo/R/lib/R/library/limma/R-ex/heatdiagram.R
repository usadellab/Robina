### Name: heatdiagram
### Title: Stemmed Heat Diagram
### Aliases: heatdiagram heatDiagram
### Keywords: hplot

### ** Examples

library(sma)
data(MouseArray)
MA <- normalizeWithinArrays(mouse.data,layout=mouse.setup)
design <- cbind(c(1,1,1,0,0,0),c(0,0,0,1,1,1))
fit <- lmFit(MA,design=design)
contrasts.mouse <- cbind(Control=c(1,0),Mutant=c(0,1),Difference=c(-1,1))
fit <- eBayes(contrasts.fit(fit,contrasts=contrasts.mouse))
results <- decideTests(fit,method="global",p=0.1)
heatDiagram(results,fit$coef,primary="Difference")



