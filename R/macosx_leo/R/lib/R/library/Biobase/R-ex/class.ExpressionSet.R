### Name: ExpressionSet
### Title: Class to Contain and Describe High-Throughput Expression Level
###   Assays.
### Aliases: class:ExpressionSet ExpressionSet ExpressionSet-class
###   exprs,ExpressionSet-method exprs<-,ExpressionSet,matrix-method
###   initialize,ExpressionSet-method coerce,exprSet,ExpressionSet-method
###   coerce,eSet,ExpressionSet-method
###   coerce,ExpressionSet,data.frame-method esApply,ExpressionSet-method
###   updateObject,ExpressionSet-method
###   makeDataPackage,ExpressionSet-method as.data.frame.ExpressionSet
###   write.exprs write.exprs,ExpressionSet-method
### Keywords: classes

### ** Examples

# create an instance of ExpressionSet
new("ExpressionSet")

new("ExpressionSet",
    exprs=matrix(runif(1000), nrow=100, ncol=10))

# update an existing ExpressionSet
data(sample.ExpressionSet)
updateObject(sample.ExpressionSet)

# information about assay and sample data
featureNames(sample.ExpressionSet)[1:10]
sampleNames(sample.ExpressionSet)[1:5]
phenoData(sample.ExpressionSet)
experimentData(sample.ExpressionSet)

# subset: first 10 genes, samples 2, 4, and 10
expressionSet <- sample.ExpressionSet[1:10,c(2,4,10)]

# named features and their expression levels
subset <- expressionSet[c("AFFX-BioC-3_at","AFFX-BioDn-5_at"),]
exprs(subset)

# samples with above-average 'score' in phenoData
highScores <- expressionSet$score > mean(expressionSet$score)
expressionSet[,highScores]

# (automatically) coerce to data.frame
lm(score~AFFX.BioDn.5_at + AFFX.BioC.3_at, data=subset)



