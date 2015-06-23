###################################################
### chunk number 1: 
###################################################
library(marray)
data(swirl) 
maPlate(swirl)<-maCompPlate(swirl,n=384) 


###################################################
### chunk number 2: 
###################################################
Gcol<- maPalette(low="white", high="green",k=50) 
Rcol<- maPalette(low="white", high="red", k=50) 
RGcol<-maPalette(low="green", high="red", k=50) 


###################################################
### chunk number 3: maImageGb
###################################################
tmp<-image(swirl[,3], xvar="maGb", subset=TRUE, col=Gcol,contours=FALSE, bar=FALSE) 


###################################################
### chunk number 4: maImageRb
###################################################
tmp<-image(swirl[,3], xvar="maRb", subset=TRUE, col=Rcol, contours=FALSE, bar=FALSE) 


###################################################
### chunk number 5: maImageMraw1
###################################################
tmp<-image(swirl[,3], xvar="maM", bar=FALSE, main="Swirl array 93: image of pre--normalization M") 


###################################################
### chunk number 6: maImageMraw2
###################################################
tmp<-image(swirl[,3], xvar="maM", subset=maTop(maM(swirl[,3]), h=0.10,
l=0.10), col=RGcol, contours=FALSE, bar=FALSE,main="Swirl array 93:
image of pre--normalization M for \% 10 tails")  


###################################################
### chunk number 7: maImageSpotCol
###################################################
tmp<- image(swirl[,3], xvar="maSpotCol", bar=FALSE) 


###################################################
### chunk number 8: maImagePrintTip
###################################################
tmp<- image(swirl[,3], xvar="maPrintTip", bar=FALSE) 


###################################################
### chunk number 9: maImageControls
###################################################
tmp<- image(swirl[,3], xvar="maControls",col=heat.colors(10),bar=FALSE) 


###################################################
### chunk number 10: maImagePlate
###################################################
tmp<- image(swirl[,3], xvar="maPlate",bar=FALSE) 


###################################################
### chunk number 11: maBoxplot1pre
###################################################
boxplot(swirl[,3], xvar="maPrintTip", yvar="maM", main="Swirl array 93: pre--normalization") 


###################################################
### chunk number 12: maBoxplot2pre
###################################################
boxplot(swirl, yvar="maM", main="Swirl arrays: pre--normalization") 


###################################################
### chunk number 13: 
###################################################
swirl.norm <- maNorm(swirl, norm="p")


###################################################
### chunk number 14: maBoxplot1post
###################################################
boxplot(swirl.norm[,3], xvar="maPrintTip", yvar="maM",
	main="Swirl array 93: post--normalization") 


###################################################
### chunk number 15: maBoxplot2post
###################################################
boxplot(swirl.norm, yvar="maM", col="green", main="Swirl arrays: post--normalization") 


###################################################
### chunk number 16: maPlot1pre
###################################################
defs<-maDefaultPar(swirl[,3],x="maA",y="maM",z="maPrintTip")

# Function for plotting the legend
legend.func<-do.call("maLegendLines",defs$def.legend)

# Function for performing and plotting lowess fits
lines.func<-do.call("maLowessLines",c(list(TRUE,f=0.3),defs$def.lines))

plot(swirl[,3], xvar="maA", yvar="maM", zvar="maPrintTip",
		      lines.func,
		      text.func=maText(),
		      legend.func,
		      main="Swirl array 93: pre--normalization MA--plot") 


###################################################
### chunk number 17: maPlot1post
###################################################
plot(swirl.norm[,3], xvar="maA", yvar="maM", zvar="maPrintTip",
		      lines.func,
		      text.func=maText(),
		      legend.func,
		      main="Swirl array 93: post--normalization MA--plot") 


