###################################################
### chunk number 1: 
###################################################
library(marray)
data(swirl)


###################################################
### chunk number 2: 
###################################################
slotNames("marrayLayout")
slotNames(swirl)


###################################################
### chunk number 3: 
###################################################
zebra.RG<-as.data.frame(cbind(c("swirl","WT","swirl","WT"), c("WT","swirl","WT","swirl"))) 
dimnames(zebra.RG)[[2]]<-c("Cy3","Cy5")
zebra.samples<-new("marrayInfo",
		    maLabels=paste("Swirl array ",1:4,sep=""), 
		    maInfo=zebra.RG,
		    maNotes="Description of targets for Swirl experiment")
zebra.samples


###################################################
### chunk number 4: 
###################################################
L<-slot(swirl, "maLayout")
L@maNgr


###################################################
### chunk number 5:  eval=FALSE
###################################################
## showMethods(classes="marrayLayout")
## showMethods("summary",classes="marrayLayout") 


###################################################
### chunk number 6: 
###################################################
showMethods("print")


###################################################
### chunk number 7: 
###################################################
summary(swirl)


###################################################
### chunk number 8: 
###################################################
swirl[1:100,2:3]


###################################################
### chunk number 9: 
###################################################
swirl.layout<-maLayout(swirl)
maNspots(swirl)
maNspots(swirl.layout)
maNgr(swirl)
maNgc(swirl.layout)
maPrintTip(swirl[1:10,3])


###################################################
### chunk number 10: 
###################################################
maNotes(swirl.layout)
maNotes(swirl.layout)<- "New value"
maNotes(swirl.layout)


###################################################
### chunk number 11: 
###################################################
L<-new("marrayLayout")
L
maNgr(L)<-4


###################################################
### chunk number 12: 
###################################################
swirl.norm<-as(swirl, "marrayNorm")    


###################################################
### chunk number 13: 
###################################################
maPlate(swirl)<-maCompPlate(swirl,n=384)


