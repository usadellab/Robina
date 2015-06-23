### Name: modifyWeights
### Title: modifyWeights
### Aliases: modifyWeights
### Keywords: hplot

### ** Examples

w <- matrix(runif(6*3),6,3)
status <- c("Gene","Gene","Ratio_Control","Ratio_Control","Gene","Gene")
modifyWeights(w,status,values="Ratio_Control",multipliers=0)



