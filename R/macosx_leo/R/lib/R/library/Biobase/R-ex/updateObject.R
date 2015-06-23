### Name: updateObject
### Title: Update an object to its current class definition
### Aliases: updateObject updateObjectTo updateObject,ANY-method
###   updateObject,list-method updateObject,environment-method
###   updateObjectTo,ANY,ANY-method updateObjectFromSlots getObjectSlots
### Keywords: manip

### ** Examples

## update object, same class
data(sample.ExpressionSet)
obj <- updateObject(sample.ExpressionSet)

setClass("UpdtA", representation(x="numeric"), contains="data.frame")
setMethod("updateObject", signature(object="UpdtA"),
          function(object, ..., verbose=FALSE) {
              if (verbose) message("updateObject object = 'A'")
              object <- callNextMethod()
              object@x <- -object@x
              object
})

a <- new("UpdtA", x=1:10)
## See steps involved
updateObject(a)

removeClass("UpdtA")
removeMethod("updateObject", "UpdtA")



