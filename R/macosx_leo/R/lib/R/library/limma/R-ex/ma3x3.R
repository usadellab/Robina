### Name: ma3x3
### Title: Two dimensional Moving Averages with 3x3 Window
### Aliases: ma3x3.matrix ma3x3.spottedarray
### Keywords: smooth

### ** Examples

x <- matrix(c(2,5,3,1,6,3,10,12,4,6,4,8,2,1,9,0),4,4)
ma3x3.matrix(x,FUN="mean")
ma3x3.matrix(x,FUN="min")



