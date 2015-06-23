### Name: nsFilter
### Title: Non-Specific-ly Filter an ExpressionSet
### Aliases: nsFilter varFilter featureFilter nsFilter,ExpressionSet-method
### Keywords: manip

### ** Examples

  library("hgu95av2.db")
  data(sample.ExpressionSet)
  ans <- nsFilter(sample.ExpressionSet)
  ans$eset
  ans$filter.log

  ## skip variance-based filtering
  ans <- nsFilter(sample.ExpressionSet, var.filter=FALSE)

  a1 <- varFilter(sample.ExpressionSet)
  a2 <- featureFilter(sample.ExpressionSet)



