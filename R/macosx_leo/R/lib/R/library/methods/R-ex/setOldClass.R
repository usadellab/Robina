### Name: setOldClass
### Title: Specify Names for Old-Style Classes
### Aliases: setOldClass .setOldIs POSIXct-class POSIXlt-class POSIXt-class
###   aov-class maov-class anova-class anova.glm-class anova.glm.null-class
###   data.frame-class density-class dump.frames-class factor-class
###   formula-class glm-class glm.null-class hsearch-class integrate-class
###   libraryIQR-class lm-class logLik-class mlm-class mtable-class
###   mts-class ordered-class packageIQR-class packageInfo-class
###   recordedplot-class rle-class socket-class summary.table-class
###   oldClass-class .OldClassesList table-class
###   initialize,data.frame-method initialize,factor-method
###   initialize,ordered-method initialize,table-method
###   initialize,summary.table-method
### Keywords: programming methods

### ** Examples

## Don't show: 
## VOODO Why is this needed?
removeMethods("length")
## End Don't show
require(stats)
setOldClass(c("mlm", "lm"))
setGeneric("dfResidual", function(model)standardGeneric("dfResidual"))
setMethod("dfResidual", "lm", function(model)model$df.residual)

## dfResidual will work on mlm objects as well as lm objects
myData <- data.frame(time = 1:10, y = (1:10)^.5)
myLm <- lm(cbind(y, y^3)  ~ time, myData)

## Don't show: 
stopifnot(identical(dfResidual(myLm), myLm$df.residual))
## End Don't show

rm(myData, myLm)
removeGeneric("dfResidual")
## Not run: setOldClass("data.frame", prototoype = data.frame())
##D 
## End(Not run)



