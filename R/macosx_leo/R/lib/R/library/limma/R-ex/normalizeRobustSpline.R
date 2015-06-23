### Name: normalizeRobustSpline
### Title: Normalize Single Microarray Using Shrunk Robust Splines
### Aliases: normalizeRobustSpline
### Keywords: models

### ** Examples

library(sma)
data(MouseArray)
MA <- MA.RG(mouse.data)
normM <- normalizeRobustSpline(MA$M[,1],MA$A[,1],mouse.setup)



