### Name: lmscFit
### Title: Fit Linear Model to Individual Channels of Two-Color Data
### Aliases: lmscFit
### Keywords: models regression

### ** Examples

library(sma)
# Subset of data from ApoAI case study in Limma User's Guide
data(MouseArray)
# Avoid non-positive intensities
RG <- backgroundCorrect(mouse.data,method="normexp")
MA <- normalizeWithinArrays(RG,mouse.setup)
MA <- normalizeBetweenArrays(MA,method="Aq")
# Randomly choose 500 genes for this example
i <- sample(1:nrow(MA),500)
MA <- MA[i,]
targets <- data.frame(Cy3=I(rep("Pool",6)),Cy5=I(c("WT","WT","WT","KO","KO","KO")))
targets.sc <- targetsA2C(targets)
targets.sc$Target <- factor(targets.sc$Target,levels=c("Pool","WT","KO"))
design <- model.matrix(~Target,data=targets.sc)
corfit <- intraspotCorrelation(MA,design)
fit <- lmscFit(MA,design,correlation=corfit$consensus)
cont.matrix <- cbind(KOvsWT=c(0,-1,1))
fit2 <- contrasts.fit(fit,cont.matrix)
fit2 <- eBayes(fit2)
topTable(fit2,adjust="fdr")



