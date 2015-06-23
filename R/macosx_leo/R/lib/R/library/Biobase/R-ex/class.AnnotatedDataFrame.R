### Name: AnnotatedDataFrame
### Title: Class Containing Measured Variables and Their Meta-Data
###   Description.
### Aliases: class:AnnotatedDataFrame AnnotatedDataFrame
###   AnnotatedDataFrame-class dimLabels dimLabels<-
###   [,AnnotatedDataFrame-method [[<-,AnnotatedDataFrame-method
###   [[,AnnotatedDataFrame-method $<-,AnnotatedDataFrame-method
###   $,AnnotatedDataFrame-method
###   combine,AnnotatedDataFrame,AnnotatedDataFrame-method
###   initialize,AnnotatedDataFrame-method
###   coerce,data.frame,AnnotatedDataFrame-method
###   coerce,phenoData,AnnotatedDataFrame-method
###   dim,AnnotatedDataFrame-method dimLabels,AnnotatedDataFrame-method
###   dimLabels<-,AnnotatedDataFrame,character-method
###   ncol,AnnotatedDataFrame-method
###   pData<-,AnnotatedDataFrame,data.frame-method
###   pData,AnnotatedDataFrame-method
###   varMetadata<-,AnnotatedDataFrame,data.frame-method
###   varMetadata,AnnotatedDataFrame-method
###   sampleNames<-,AnnotatedDataFrame,ANY-method
###   sampleNames,AnnotatedDataFrame-method
###   featureNames,AnnotatedDataFrame-method
###   featureNames<-,AnnotatedDataFrame-method
###   show,AnnotatedDataFrame-method updateObject,AnnotatedDataFrame-method
###   varLabels<-,AnnotatedDataFrame-method
###   varLabels,AnnotatedDataFrame-method
### Keywords: classes

### ** Examples

df <- data.frame(x=1:6,
                 y=rep(c("Low", "High"),3),
                 z=I(LETTERS[1:6]),
                 row.names=paste("Sample", 1:6, sep="_"))
metaData <-
  data.frame(labelDescription=c(
               "Numbers",
               "Factor levels",
               "Characters"))
                       
new("AnnotatedDataFrame")
new("AnnotatedDataFrame", data=df)
new("AnnotatedDataFrame",
    data=df, varMetadata=metaData)

as(df, "AnnotatedDataFrame")

obj <- new("AnnotatedDataFrame")
pData(obj) <- df
varMetadata(obj) <- metaData
validObject(obj)



