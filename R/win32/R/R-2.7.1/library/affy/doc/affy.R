###################################################
### chunk number 1: 
###################################################
library(affy)


###################################################
### chunk number 2: 
###################################################
bgcorrect.methods


###################################################
### chunk number 3: 
###################################################
data(affybatch.example) ##data included in the package for examples
normalize.methods(affybatch.example)


###################################################
### chunk number 4: 
###################################################
pmcorrect.methods


###################################################
### chunk number 5: 
###################################################
express.summary.stat.methods


###################################################
### chunk number 6: 
###################################################
eset <- mas5(affybatch.example)


###################################################
### chunk number 7: 
###################################################
Calls <- mas5calls(affybatch.example)


###################################################
### chunk number 8: 
###################################################
eset <- rma(affybatch.example)


###################################################
### chunk number 9: 
###################################################
affybatch.example


###################################################
### chunk number 10: 
###################################################
phenoData(affybatch.example)
pData(affybatch.example)


###################################################
### chunk number 11: 
###################################################
library(geneplotter)
data(affybatch.example)
MAplot(affybatch.example,pairs=TRUE,plot.method="smoothScatter")


###################################################
### chunk number 12: 
###################################################
Index <- c(1,2,3,100,1000,2000) ##6 arbitrary probe positions
pm(affybatch.example)[Index,]
mm(affybatch.example)[Index,]
probeNames(affybatch.example)[Index]


###################################################
### chunk number 13: 
###################################################
sampleNames(affybatch.example)


###################################################
### chunk number 14: 
###################################################
mean(mm(affybatch.example)>pm(affybatch.example))


###################################################
### chunk number 15: 
###################################################
gn <- geneNames(affybatch.example)
pm(affybatch.example,gn[100])


###################################################
### chunk number 16: 
###################################################
hist(affybatch.example[,1:2]) ##PM histogram of arrays 1 and 2


###################################################
### chunk number 17: 
###################################################
par(mfrow=c(2,2))
image(affybatch.example)


###################################################
### chunk number 18: 
###################################################
par(mfrow=c(1,1))
boxplot(affybatch.example,col=c(2,3,4))


###################################################
### chunk number 19: 
###################################################
deg <- AffyRNAdeg(affybatch.example)
names(deg)


###################################################
### chunk number 20: 
###################################################
summaryAffyRNAdeg(deg)


###################################################
### chunk number 21: 
###################################################
plotAffyRNAdeg(deg)


###################################################
### chunk number 22: 
###################################################
affybatch.example.normalized <- normalize(affybatch.example)


###################################################
### chunk number 23: 
###################################################
gn <- featureNames(affybatch.example)
ps <- probeset(affybatch.example,gn[1:2])
#this is what i should be using: ps
show(ps[[1]])


###################################################
### chunk number 24: 
###################################################
mylocation <- list("AB000114_at"=cbind(pm=c(1,2,3),mm=c(4,5,6)),
                   "AB000115_at"=cbind(pm=c(4,5,6),mm=c(1,2,3)))


###################################################
### chunk number 25: 
###################################################
ps <- probeset(affybatch.example,genenames=c("AB000114_at","AB000115_at"),locations=mylocation)


###################################################
### chunk number 26: 
###################################################
pm(ps[[1]])
mm(ps[[1]])
pm(ps[[2]])
mm(ps[[2]])


###################################################
### chunk number 27: 
###################################################
data(SpikeIn) ##SpikeIn is a ProbeSets
pms <- pm(SpikeIn)
mms <- mm(SpikeIn)

##pms follow concentration
par(mfrow=c(1,2))
concentrations <- matrix(as.numeric(sampleNames(SpikeIn)),20,12,byrow=TRUE)
matplot(concentrations,pms,log="xy",main="PM",ylim=c(30,20000))
lines(concentrations[1,],apply(pms,2,mean),lwd=3)
##so do mms
matplot(concentrations,mms,log="xy",main="MM",ylim=c(30,20000))
lines(concentrations[1,],apply(mms,2,mean),lwd=3)


###################################################
### chunk number 28: 
###################################################
cat("HG_U95Av2 is",cleancdfname("HG_U95Av2"),"\n")
cat("HG-133A is",cleancdfname("HG-133A"),"\n")


###################################################
### chunk number 29: 
###################################################
cat("HG_U95Av2 is",cleancdfname("HG_U95Av2",addcdf=FALSE),"\n")


###################################################
### chunk number 30: 
###################################################
data(cdfenv.example)
ls(cdfenv.example)[1:5]
get(ls(cdfenv.example)[1],cdfenv.example)


###################################################
### chunk number 31: 
###################################################
print(affybatch.example@cdfName)
myenv <- getCdfInfo(affybatch.example)
ls(myenv)[1:5]


###################################################
### chunk number 32: 
###################################################
print(affybatch.example@cdfName)
myenv <- getCdfInfo(affybatch.example)
ls(myenv)[1:5]


###################################################
### chunk number 33: 
###################################################
Index <- pmindex(affybatch.example)
names(Index)[1:2]
Index[1:2]


###################################################
### chunk number 34: 
###################################################
pmindex(affybatch.example,genenames=c("AB000114_at","AB000115_at"))


###################################################
### chunk number 35: 
###################################################
mmindex(affybatch.example,genenames=c("AB000114_at","AB000115_at"))


###################################################
### chunk number 36: 
###################################################
indexProbes(affybatch.example,which="pm")[1]
indexProbes(affybatch.example,which="mm")[1]
indexProbes(affybatch.example,which="both")[1]


###################################################
### chunk number 37: 
###################################################
opt <- getOption("BioC")
affy.opt <- opt$affy
print(names(affy.opt))


###################################################
### chunk number 38: 
###################################################
opt <- getOption("BioC")
affy.opt <- opt$affy
affy.opt$normalize.method <- "constant"
opt$affy <- affy.opt
options(BioC=opt)


###################################################
### chunk number 39: 
###################################################
opt <- getOption("BioC")
affy.opt <- opt$affy
affy.opt$compress.cel <- TRUE
opt$affy <- affy.opt
options(BioC=opt)


