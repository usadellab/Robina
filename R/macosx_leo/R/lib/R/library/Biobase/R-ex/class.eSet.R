### Name: eSet
### Title: Class to Contain High-Throughput Assays and Experimental
###   Metadata
### Aliases: class:eSet eSet eSet-class [,eSet-method $,eSet-method
###   $<-,eSet-method [[,eSet-method [[<-,eSet-method abstract,eSet-method
###   annotation,eSet-method annotation<-,eSet,character-method
###   assayData,eSet-method assayData<-,eSet,AssayData-method
###   assayDataElement assayDataElement<- assayDataElementNames
###   assayDataElementReplace combine,eSet,ANY-method
###   combine,eSet,eSet-method description,eSet-method
###   description<-,eSet,MIAME-method dim,eSet-method dims,eSet-method
###   experimentData,eSet-method experimentData<-,eSet,MIAME-method
###   exprs,eSet-method exprs<-,eSet,AssayData-method
###   featureData,eSet-method featureData<-,eSet,AnnotatedDataFrame-method
###   featureNames,eSet-method featureNames<-,eSet-method fData,eSet-method
###   fData<-,eSet,data.frame-method fvarLabels,eSet-method
###   fvarLabels<-,eSet-method fvarMetadata,eSet-method
###   fvarMetadata<-,eSet,data.frame-method initialize,eSet-method
###   ncol,eSet-method notes,eSet-method notes<-,eSet,ANY-method
###   pData,eSet-method pData<-,eSet,data.frame-method
###   phenoData,eSet-method phenoData<-,eSet,AnnotatedDataFrame-method
###   pubMedIds,eSet-method pubMedIds<-,eSet,character-method
###   preproc,eSet-method preproc<-,eSet-method sampleNames,eSet-method
###   sampleNames<-,eSet,ANY-method show,eSet-method
###   storageMode,eSet-method storageMode<-,eSet,character-method
###   varLabels,eSet-method varLabels<-,eSet-method varMetadata,eSet-method
###   varMetadata<-,eSet,data.frame-method storageMode,eSet-method
###   storageMode<-,eSet,character-method updateObject,eSet-method
###   updateObjectTo,eSet,eSet-method listOrEnv SW
### Keywords: classes

### ** Examples


# update previous eSet-like class oldESet to existing derived class
## Not run: updateOldESet(oldESet, "ExpressionSet")

# create a new, ad hoc, class, for personal use
# all methods outlined above are available automatically
setClass("MySet", contains="eSet")
new("MySet")

# Create a more robust class, with initialization and validation methods
# to ensure assayData contains specific matricies
setClass("TwoColorSet", contains="eSet")

setMethod("initialize", "TwoColorSet",
          function(.Object,
                   phenoData = new("AnnotatedDataFrame"),
                   experimentData = new("MIAME"),
                   annotation = character(),
                   R = new("matrix"),
                   G = new("matrix"),
                   Rb = new("matrix"),
                   Gb = new("matrix"),
                   ... ) {
            callNextMethod(.Object,
                           phenoData = phenoData,
                           experimentData = experimentData,
                           annotation = annotation,
                           R=R, G=G, Rb=Rb, Gb=Gb,
                           ...)
          })

setValidity("TwoColorSet", function(object) {
  assayDataValidMembers(assayData(object), c("R", "G", "Rb", "Gb"))
})

new("TwoColorSet")

# eSet objects cannot be instantiated directly, only derived objects
try(new("eSet"))

removeClass("MySet")
removeClass("TwoColorSet")
removeMethod("initialize", "TwoColorSet")



