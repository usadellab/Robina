### Name: rowROC-class
### Title: Class "rowROC"
### Aliases: rowROC-class pAUC AUC sens spec area
###   pAUC,rowROC,numeric-method plot,rowROC,missing-method
###   AUC,rowROC-method spec,rowROC-method sens,rowROC-method
###   area,rowROC-method show,rowROC-method [,rowROC-method
### Keywords: classes

### ** Examples

require(genefilter)
data(sample.ExpressionSet)
roc <- rowpAUCs(sample.ExpressionSet, "sex", p=0.5)
roc
area(roc[1:3])

if(interactive()) {
par(ask=TRUE)
plot(roc)
plot(1-spec(roc[1]), sens(roc[2]))
par(ask=FALSE)
}

pAUC(roc, 0.1)
roc



