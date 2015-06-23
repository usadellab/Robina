###################################################
### chunk number 1: 
###################################################
library(marray)
dir(system.file("swirldata", package="marray")) 


###################################################
### chunk number 2: 
###################################################
datadir <- system.file("swirldata", package="marray")
swirlTargets <- read.marrayInfo(file.path(datadir, "SwirlSample.txt"))


###################################################
### chunk number 3: 
###################################################
mraw <- read.Spot(targets = swirlTargets, path=datadir)


###################################################
### chunk number 4: 
###################################################
galinfo <- read.Galfile("fish.gal", path=datadir)
mraw@maLayout <- galinfo$layout
mraw@maGnames <- galinfo$gnames


