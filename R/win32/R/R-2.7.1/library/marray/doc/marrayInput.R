###################################################
### chunk number 1: 
###################################################
library(marray)
data(swirl)


###################################################
### chunk number 2: 
###################################################
datadir <- system.file("swirldata", package="marray")
dir(datadir)


###################################################
### chunk number 3: 
###################################################
swirlTargets <- read.marrayInfo(file.path(datadir, "SwirlSample.txt"))
summary(swirlTargets)


###################################################
### chunk number 4: 
###################################################
galinfo <- read.Galfile("fish.gal", path=datadir) 
names(galinfo)


###################################################
### chunk number 5: 
###################################################
swirl.gnames <- read.marrayInfo(file.path(datadir, "fish.gal"),
info.id=4:5, labels=5, skip=21) 
summary(swirl.gnames) 


###################################################
### chunk number 6: 
###################################################
swirl.layout <- read.marrayLayout(fname=file.path(datadir, "fish.gal"),
                                  ngr=4, ngc=4, nsr=22, nsc=24,
                                  skip=21,ctl.col=4)
ctl<-rep("Control",maNspots(swirl.layout))
ctl[maControls(swirl.layout)!="control"]  <- "probes"
maControls(swirl.layout)<-factor(ctl)
summary(swirl.layout)


###################################################
### chunk number 7: 
###################################################
mraw <- read.Spot(path=datadir, 
                  layout=galinfo$layout, 
                  gnames=galinfo$gnames, 
                  target=swirlTargets)
summary(mraw)


