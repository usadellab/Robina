### Name: generateExprSet-method
### Title: generate a set of expression values
### Aliases: generateExprSet-methods computeExprSet generateExprSet.methods
### Keywords: manip

### ** Examples

 data(affybatch.example)

 ids <- c( "A28102_at","AB000114_at")
  
  eset <- computeExprSet(affybatch.example, pmcorrect.method="pmonly",
summary.method="avgdiff", ids=ids)



