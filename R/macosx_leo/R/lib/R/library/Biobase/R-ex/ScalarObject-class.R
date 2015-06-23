### Name: ScalarObject-class
### Title: Utility classes for length one (scalar) objects
### Aliases: ScalarObject-class ScalarCharacter-class ScalarInteger-class
###   ScalarNumeric-class mkScalar show,ScalarObject-method
###   show,ScalarCharacter-method
### Keywords: classes

### ** Examples

v <- list(mkScalar("a single string"),
          mkScalar(1),
          mkScalar(1L))
sapply(v, class)
sapply(v, length)



