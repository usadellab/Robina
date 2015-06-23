### Name: auROC
### Title: Area Under Receiver Operating Curve
### Aliases: auROC
### Keywords: htest

### ** Examples

auROC(c(1,1,0,0,0))
truth <- rbinom(30,size=1,prob=0.2)
stat <- rchisq(30,df=2)
auROC(truth,stat)



