### Name: printtipWeights
### Title: Sub-array Quality Weights
### Aliases: printtipWeights printtipWeightsSimple
### Keywords: regression models

### ** Examples

library(sma)
# Subset of data from ApoAI case study in Limma User's Guide
data(MouseArray)
# Avoid non-positive intensities
RG <- backgroundCorrect(mouse.data, method="half")
MA <- normalizeWithinArrays(RG, mouse.setup)
MA <- normalizeBetweenArrays(MA, method="Aq")
targets <- data.frame(Cy3=I(rep("Pool",6)),Cy5=I(c("WT","WT","WT","KO","KO","KO")))
design <- modelMatrix(targets, ref="Pool")
subarrayw <- printtipWeights(MA, design, layout=mouse.setup)
fit <- lmFit(MA, design, weights=subarrayw)
fit2 <- contrasts.fit(fit, contrasts=c(-1,1))
fit2 <- eBayes(fit2)
# Use of sub-array weights increases the significance of the top genes
topTable(fit2)
# Create an image plot of sub-array weights from each array
zlim <- c(min(subarrayw), max(subarrayw))
par(mfrow=c(3,2), mai=c(0.1,0.1,0.3,0.1))
for(i in 1:6) 
        imageplot(subarrayw[,i], layout=mouse.setup, zlim=zlim, main=paste("Array", i))



