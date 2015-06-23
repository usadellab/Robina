### Name: rowpAUCs-methods
### Title: Rowwise ROC and pAUC computation
### Aliases: rowpAUCs-methods rowpAUCs rowpAUCs,matrix,factor-method
###   rowpAUCs,matrix,numeric-method rowpAUCs,ExpressionSet,ANY-method
###   rowpAUCs,ExpressionSet,character-method
### Keywords: math

### ** Examples

data(sample.ExpressionSet)

r1 = rowttests(sample.ExpressionSet, "sex")
r2 = rowpAUCs(sample.ExpressionSet, "sex", p=0.1)

plot(area(r2, total=TRUE), r1$statistic, pch=16)
sel <- which(area(r2, total=TRUE) > 0.7)
plot(r2[sel])

## this compares performance and output of rowpAUCs to function pAUC in
## package ROC 
if(require(ROC)){
  ## performance
  myRule = function(x)
    pAUC(rocdemo.sca(truth = as.integer(sample.ExpressionSet$sex)-1 ,
         data = x, rule = dxrule.sca), t0 = 0.1)
  nGenes = 200
  cat("computation time for ", nGenes, "genes:\n")
  cat("function pAUC: ")
  print(system.time(r3 <- esApply(sample.ExpressionSet[1:nGenes, ], 1, myRule)))
  cat("function rowpAUCs: ")
  print(system.time(r2 <- rowpAUCs(sample.ExpressionSet[1:nGenes, ],
  "sex", p=1)))

  ## compare output
  myRule2 = function(x)
   pAUC(rocdemo.sca(truth = as.integer(sample.ExpressionSet$sex)-1 ,
                    data = x, rule = dxrule.sca), t0 = 1)
  r4 <-  esApply(sample.ExpressionSet[1:nGenes, ], 1, myRule2)
  plot(r4,area(r2), xlab="function pAUC", ylab="function rowpAUCs",
  main="pAUCs")

  plot(r4, area(rowpAUCs(sample.ExpressionSet[1:nGenes, ],
  "sex", p=1, flip=FALSE)), xlab="function pAUC", ylab="function rowpAUCs",
  main="pAUCs")

  r4[r4<0.5] <- 1-r4[r4<0.5]
  plot(r4, area(r2), xlab="function pAUC", ylab="function rowpAUCs",
  main="pAUCs")
 }



