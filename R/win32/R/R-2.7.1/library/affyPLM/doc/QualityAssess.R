###################################################
### chunk number 1: loadPackage
###################################################
library(affyPLM)
options(width=40)


###################################################
### chunk number 2: loadData
###################################################
require(affydata)
data(Dilution)   # an example dataset provided by the affydata package
#FIXME:remove the next line
Dilution = updateObject(Dilution)
Pset <- fitPLM(Dilution)


###################################################
### chunk number 3: weightsImageShow eval=FALSE
###################################################
## image(Pset,which=2)


###################################################
### chunk number 4: weightsImageDo
###################################################
bitmap("Quality-weightimage1a.png",height=4,width=4,pointsize=10,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2)
dev.off()


###################################################
### chunk number 5: weightscolorImageShow eval=FALSE
###################################################
## image(Pset,which=2,col=gray(0:25/25),add.legend=TRUE)
## image(Pset,which=2,col=gray(25:0/25),add.legend=TRUE)


###################################################
### chunk number 6: weightscolorImageDo
###################################################
bitmap("Quality-weightimage2a.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2,col=gray(0:25/25),add.legend=T)
dev.off()

bitmap("Quality-weightimage2b.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2,col=gray(25:0/25),add.legend=T)
dev.off()


###################################################
### chunk number 7: residualImageShow eval=FALSE
###################################################
## image(Pset,which=2, type="resids")
## image(Pset,which=2, type="pos.resids")
## image(Pset,which=2, type="neg.resids")
## image(Pset,which=2, type="sign.resids")


###################################################
### chunk number 8: residualImageDo
###################################################
bitmap("Quality-residualimages1.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2, type="resids")
dev.off()
bitmap("Quality-residualimages2.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2, type="pos.resids")
dev.off()
bitmap("Quality-residualimages3.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2, type="neg.resids")
dev.off()
bitmap("Quality-residualimages4.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2, type="sign.resids")
dev.off()


###################################################
### chunk number 9: residualcolorImageShow eval=FALSE
###################################################
## image(Pset,which=2,type="resids",col=pseudoPalette(low="darkgreen",high="magenta",mid="lightgrey"),add.legend=TRUE)
## image(Pset,which=2,type="pos.resids",col=pseudoPalette(low="yellow",high="darkblue"),add.legend=TRUE)


###################################################
### chunk number 10: residualcolorImageDo
###################################################
bitmap("Quality-residualimages5.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2,type="resids",col=pseudoPalette(low="darkgreen",high="magenta",mid="lightgrey"),add.legend=TRUE)
dev.off()
bitmap("Quality-residualimages6.png",height=4,width=4,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
image(Pset,which=2,type="pos.resids",col=pseudoPalette(low="yellow",high="darkblue"),add.legend=TRUE)
dev.off()


###################################################
### chunk number 11: RLEShow eval=FALSE
###################################################
## RLE(Pset,main="RLE for Dilution dataset")


###################################################
### chunk number 12: RLEDo
###################################################
bitmap("Quality-RLE.png",height=4,width=4,pointsize=10,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
RLE(Pset,main="RLE for Dilution dataset")
dev.off()


###################################################
### chunk number 13: rleStat
###################################################
RLE(Pset,type="stats")


###################################################
### chunk number 14: NUSEShow eval=FALSE
###################################################
## NUSE(Pset,main="NUSE for Dilution dataset")


###################################################
### chunk number 15: NUSEDo
###################################################
bitmap("Quality-NUSE.png",height=4,width=4,pointsize=10,res=300)
par(mar=c(2.0,2.1,1.6,1.1),oma=c(1,1,0,0))
NUSE(Pset,main="NUSE for Dilution dataset")
dev.off()


###################################################
### chunk number 16: nuseStat
###################################################
 NUSE(Pset,type="stats")


