### Name: AffyBatch-class
### Title: Class AffyBatch
### Aliases: AffyBatch-class AffyBatch,ANY AffyBatch probes geneNames
###   geneNames<- getCdfInfo image indexProbes intensity<- intensity
###   pmindex mmindex probeset $.AffyBatch cdfName cdfName,AffyBatch-method
###   checkValidFilenames probes,AffyBatch-method exprs,AffyBatch-method
###   exprs<-,AffyBatch,ANY-method se.exprs,AffyBatch-method
###   se.exprs<-,AffyBatch-method featureNames,AffyBatch-method
###   featureNames<-,AffyBatch-method geneNames,AffyBatch-method
###   geneNames<-,AffyBatch,ANY-method getCdfInfo,AffyBatch-method
###   image,AffyBatch-method initialize,AffyBatch-method
###   indexProbes,AffyBatch-method intensity<-,AffyBatch-method
###   intensity,AffyBatch-method pmindex,AffyBatch-method
###   mmindex,AffyBatch-method probeset,AffyBatch-method
###   boxplot,AffyBatch-method dim,AffyBatch-method row,AffyBatch-method
###   col,AffyBatch-method show,AffyBatch-method pm,AffyBatch-method
###   pm<-,AffyBatch,ANY-method mm,AffyBatch-method
###   mm<-,AffyBatch,ANY-method probeNames,AffyBatch-method
###   hist,AffyBatch-method [<-,AffyBatch-method [,AffyBatch-method
###   [[,AffyBatch-method length,AffyBatch-method
###   bg.correct,AffyBatch,character-method
###   indexProbes,AffyBatch,character-method
###   indexProbes,AffyBatch,missing-method
###   computeExprSet,AffyBatch,character,character-method
###   cdfName,AffyBatch-method updateObject,AffyBatch-method
### Keywords: classes

### ** Examples

## load example
data(affybatch.example)

## nice print
print(affybatch.example)

pm(affybatch.example)[1:5,]
mm(affybatch.example)[1:5,]

## get indexes for the PM probes for the affyID "A28102_at" 
mypmindex <- pmindex(affybatch.example,"A28102_at")
## same operation using the primitive
mypmindex <- indexProbes(affybatch.example, which="pm", genenames="A28102_at")[[1]]
## get the probe intensities from the index
intensity(affybatch.example)[mypmindex, ]

## load bigger example (try 'help(Dilution)' )
data(affybatch.example)
description(affybatch.example) ##we can also use the methods of eSet
sampleNames(affybatch.example)
abstract(affybatch.example)



