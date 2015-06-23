### Name: seemsS4Object
### Title: Heuristic test for an object from an S4 class
### Aliases: seemsS4Object
### Keywords: programming classes

### ** Examples
## Not run: ## this is deprecated
##D seemsS4Object(1) # FALSE
##D 
##D seemsS4Object(getClass(class(1)))  #TRUE
##D 
##D ## how to test for an S4 object that is not a vector
##D 
##D S4NotVector <-
##D     function(object) seemsS4Object(object) && !is(object, "vector")
##D 
##D setClass("classNotNumeric", representation(x="numeric", y="numeric"))
##D 
##D setClass("classWithNumeric", representation(y="numeric"),
##D          contains = "numeric")
##D 
##D obj1 <- new("classNotNumeric", x=1, y=2)
##D 
##D obj2 <- new("classWithNumeric", 1, y=2)
##D 
##D seemsS4Object(obj1); seemsS4Object(obj2)  # TRUE, TRUE
##D S4NotVector(obj1); S4NotVector(obj2)  # TRUE, FALSE
##D 
##D removeClass("classNotNumeric")
##D removeClass("classWithNumeric")
## End(Not run)


