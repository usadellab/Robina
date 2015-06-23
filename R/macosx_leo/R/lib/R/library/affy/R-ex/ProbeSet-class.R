### Name: ProbeSet-class
### Title: Class ProbeSet
### Aliases: ProbeSet-class mm,ProbeSet-method mm<-,ProbeSet,matrix-method
###   pm<-,ProbeSet,matrix-method pm,ProbeSet-method show,ProbeSet-method
###   barplot,ProbeSet-method colnames,ProbeSet-method
###   express.summary.stat,ProbeSet,character,character-method
###   sampleNames,ProbeSet-method
### Keywords: classes

### ** Examples

data(affybatch.example)
ps <- probeset(affybatch.example,geneNames(affybatch.example)[1:2])
names(ps)
print(ps[[1]])



