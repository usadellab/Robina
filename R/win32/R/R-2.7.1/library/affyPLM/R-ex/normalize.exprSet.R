### Name: normalize.ExpressionSet
### Title: Normalization applied to ExpressionSets
### Aliases: normalize.ExpressionSet.quantiles
###   normalize.ExpressionSet.loess normalize.ExpressionSet.contrasts
###   normalize.ExpressionSet.qspline normalize.ExpressionSet.invariantset
###   normalize.ExpressionSet.scaling normalize.ExpressionSet.methods
### Keywords: manip

### ** Examples

data(affybatch.example)
eset <- rma(affybatch.example,normalize=FALSE,background=FALSE)
normalize(eset)



