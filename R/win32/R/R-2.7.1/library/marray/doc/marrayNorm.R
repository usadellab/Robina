###################################################
### chunk number 1: 
###################################################
library("marray", verbose=FALSE)
data(swirl)
maPlate(swirl)<-maCompPlate(swirl,n=384)


###################################################
### chunk number 2: 
###################################################
swirl.norm <- maNorm(swirl, norm="p")
summary(swirl.norm)


###################################################
### chunk number 3:  eval=FALSE
###################################################
## swirl.norm1 <- maNorm(swirl, norm="p")
## swirl.norm2 <- maNormScale(swirl.norm1, norm="p")


###################################################
### chunk number 4: maBoxplot1pre
###################################################
boxplot(swirl[,3], xvar="maPrintTip", yvar="maM", main="Swirl array 93: pre--normalization")


###################################################
### chunk number 5: maBoxplot2pre
###################################################
boxplot(swirl, yvar="maM", main="Swirl arrays: pre--normalization")


###################################################
### chunk number 6: maBoxplot1post
###################################################
boxplot(swirl.norm[,3], xvar="maPrintTip", yvar="maM", main="Swirl array 93: post--normalization")


###################################################
### chunk number 7: maBoxplot2post
###################################################
boxplot(swirl.norm, yvar="maM", main="Swirl arrays: post--normalization")


###################################################
### chunk number 8: maPlot1pre
###################################################
plot(swirl[,3], main="Swirl array 93: pre--normalization MA--plot")


###################################################
### chunk number 9: maPlot1post
###################################################
plot(swirl.norm[,3], main="Swirl array 93: post--normalization MA--plot")


