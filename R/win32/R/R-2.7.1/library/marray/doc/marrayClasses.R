###################################################
### chunk number 1: 
###################################################
library("marray")
data(swirl)


###################################################
### chunk number 2: R
###################################################
getClassDef("marrayLayout")


###################################################
### chunk number 3: R
###################################################
getClassDef("marrayInfo")


###################################################
### chunk number 4: R
###################################################
getClassDef("marrayRaw")


###################################################
### chunk number 5: R
###################################################
getClassDef("marrayNorm")


###################################################
### chunk number 6: 
###################################################
zebra.RG<-as.data.frame(cbind(c("swirl","WT","swirl","WT"),
c("WT","swirl","WT","swirl")))
dimnames(zebra.RG)[[2]]<-c("Cy3","Cy5")
zebra.samples<-new("marrayInfo",
		    maLabels=paste("Swirl array ",1:4,sep=""), 
		    maInfo=zebra.RG,
		    maNotes="Description of targets for Swirl experiment")
zebra.samples


###################################################
### chunk number 7: 
###################################################
L<-slot(swirl, "maLayout")
L@maNgr


###################################################
### chunk number 8: 
###################################################
slotNames("marrayLayout")
slotNames(swirl)


###################################################
### chunk number 9: 
###################################################
validObject(maLayout(swirl), test=TRUE)


###################################################
### chunk number 10:  eval=FALSE
###################################################
## showMethods(classes="marrayLayout")
## showMethods("show",classes="marrayLayout") 


###################################################
### chunk number 11: 
###################################################
showMethods("summary")


###################################################
### chunk number 12: 
###################################################
summary(swirl)


###################################################
### chunk number 13: 
###################################################
swirl[1:100,2:3]


###################################################
### chunk number 14: 
###################################################
swirl.layout<-maLayout(swirl)
maNspots(swirl)
maNspots(swirl.layout)
maNgr(swirl)
maNgc(swirl.layout)
maPrintTip(swirl[1:10,3])


###################################################
### chunk number 15: 
###################################################
maNotes(swirl.layout)
maNotes(swirl.layout)<- "New value"
maNotes(swirl.layout)


###################################################
### chunk number 16: 
###################################################
L<-new("marrayLayout")
L
maNgr(L)<-4


###################################################
### chunk number 17: 
###################################################
swirl.norm<-as(swirl, "marrayNorm")    


###################################################
### chunk number 18: 
###################################################
maPlate(swirl)<-maCompPlate(swirl,n=384)


