###################################################
### chunk number 1: loadPackage
###################################################
library(affyPLM)
options(width=60)


###################################################
### chunk number 2: makeExpressionSet
###################################################
require(affydata)
data(Dilution)
eset.Dilution <- rma(Dilution)


###################################################
### chunk number 3: FirstUsageShow eval=FALSE
###################################################
## par(mfrow=c(2,2))
## MAplot(eset.Dilution)


###################################################
### chunk number 4: FirstUsageDo
###################################################
bitmap("MAplotFirstUse.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(2,2))
MAplot(eset.Dilution)
dev.off()


###################################################
### chunk number 5: smoothScatterShow eval=FALSE
###################################################
## par(mfrow=c(2,2))
## MAplot(eset.Dilution,plot.method="smoothScatter")


###################################################
### chunk number 6: smoothScatterDo
###################################################
bitmap("MAplotsmoothScatter.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(2,2))
MAplot(eset.Dilution,plot.method="smoothScatter",nrpoints=256)
dev.off()


###################################################
### chunk number 7: RefUsageShow eval=FALSE
###################################################
## par(mfrow=c(2,2))
## MAplot(eset.Dilution,plot.method="smoothScatter",ref=1)


###################################################
### chunk number 8: RefUsageDo
###################################################
bitmap("MAplotsmoothScatterRef.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(2,2))
MAplot(eset.Dilution,ref=1,plot.method="smoothScatter",nrpoints=256)
dev.off()


###################################################
### chunk number 9: RefUsageWhichShow eval=FALSE
###################################################
## par(mfrow=c(2,1))
## MAplot(eset.Dilution,which=c(2,4),ref=1,plot.method="smoothScatter")


###################################################
### chunk number 10: RefUsageWhichShow
###################################################
bitmap("MAplotsmoothScatterWhich.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(2,1))
MAplot(eset.Dilution,which=c(2,4),ref=1,plot.method="smoothScatter",nrpoints=256)
dev.off()


###################################################
### chunk number 11: SubsetUsageWhichShow eval=FALSE
###################################################
## par(mfrow=c(2,1))
## MAplot(eset.Dilution,which=c(1,2),ref=c(1,2),plot.method="smoothScatter")


###################################################
### chunk number 12: SubsetUsageWhichDo
###################################################
bitmap("MAplotsmoothScatterSubset.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(2,1))
MAplot(eset.Dilution,which=c(1,2),ref=c(1,2),plot.method="smoothScatter",nrpoints=256)
dev.off()


###################################################
### chunk number 13: SubsetUsageWithNames
###################################################
MAplot(eset.Dilution,which=c("20A","20B"),ref=c("20A","20B"),plot.method="smoothScatter",nrpoints=256)


###################################################
### chunk number 14: calcPA
###################################################
PA.calls <- mas5calls(Dilution)
Is.Present <- exprs(PA.calls) == "P"
Number.Present <- apply(Is.Present,1,sum)


###################################################
### chunk number 15: addShow eval=FALSE
###################################################
## MAplot(eset.Dilution[Number.Present ==4,],show.statistics=FALSE,which=1,pch=20,cex=0.4,ylim=c(-0.8,0.8),xlim=c(2,15),add.loess=FALSE)
## MAplot(eset.Dilution[Number.Present ==0,],plot.method="add",col="red",which=1,pch=20,cex=0.4,show.statistics=FALSE,add.loess=FALSE)
## MAplot(eset.Dilution[Number.Present ==3,],plot.method="add",show.statistics=FALSE,which=1,col="green",pch=20,cex=0.4,add.loess=FALSE)
## MAplot(eset.Dilution[Number.Present ==2,],plot.method="add",show.statistics=FALSE,which=1,col="blue",pch=20,cex=0.4,add.loess=FALSE)
## MAplot(eset.Dilution[Number.Present ==1,],plot.method="add",show.statistics=FALSE,which=1,col="orange",pch=20,cex=0.4,add.loess=FALSE)


###################################################
### chunk number 16: addDo
###################################################
bitmap("MAplotadd.png",height=8,width=8,pointsize=10,res=300)
par(mfrow=c(1,1))
MAplot(eset.Dilution[Number.Present ==4,],show.statistics=FALSE,which=1,pch=20,cex=0.4,ylim=c(-0.8,0.8),xlim=c(2,15),add.loess=FALSE)
MAplot(eset.Dilution[Number.Present ==0,],plot.method="add",col="red",which=1,pch=20,cex=0.4,show.statistics=FALSE,add.loess=FALSE)
MAplot(eset.Dilution[Number.Present ==3,],plot.method="add",show.statistics=FALSE,which=1,col="green",pch=20,cex=0.4,add.loess=FALSE)
MAplot(eset.Dilution[Number.Present ==2,],plot.method="add",show.statistics=FALSE,which=1,col="blue",pch=20,cex=0.4,add.loess=FALSE)
MAplot(eset.Dilution[Number.Present ==1,],plot.method="add",show.statistics=FALSE,which=1,col="orange",pch=20,cex=0.4,add.loess=FALSE)
dev.off()


###################################################
### chunk number 17: groupsShow eval=FALSE
###################################################
## MAplot(eset.Dilution,groups=c("Liver 20","Liver 20","Liver 10","Liver 10"),ref="Liver 10")


###################################################
### chunk number 18: groupsDo
###################################################
bitmap("MAplotgroups.png",height=8,width=8,pointsize=10,res=300)
MAplot(eset.Dilution,groups=c("Liver 20","Liver 20","Liver 10","Liver 10"),ref="Liver 10")
dev.off()


###################################################
### chunk number 19: 
###################################################
## give ghostscript on Windows a few seconds to catch up
Sys.sleep(10)


