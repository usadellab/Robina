### Name: readExpressionSet
### Title: Read 'ExpressionSet'
### Aliases: readExpressionSet
### Keywords: file manip

### ** Examples


exprsFile = system.file("extdata", "exprsData.txt", package="Biobase")
phenoFile = system.file("extdata", "pData.txt", package="Biobase")

## Read ExpressionSet with appropriate parameters
obj = readExpressionSet(exprsFile, phenoFile, sep = "\t", header=TRUE)
obj




