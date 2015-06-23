### Name: normalizeBetweenArrays
### Title: Normalize Between Arrays
### Aliases: normalizeBetweenArrays
### Keywords: models multivariate

### ** Examples

library(sma)
data(MouseArray)
MA <- normalizeWithinArrays(mouse.data, mouse.setup)
plot.scale.box(MA$M)

#  Between array scale normalization as in Yang et al (2001):
MA <- normalizeBetweenArrays(MA,method="scale")
print(MA)
show(MA)
plot.scale.box(MA$M)

#  One can get the same results using the matrix method:
M <- normalizeBetweenArrays(MA$M,method="scale")
plot.scale.box(M)

#  MpAq normalization as in Yang and Thorne (2003):
MpAq <- normalizeWithinArrays(mouse.data, mouse.setup)
MpAq <- normalizeBetweenArrays(MpAq, method="Aq")
plotDensities(MpAq)



