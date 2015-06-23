### Name: cache
### Title: Evaluate an expression if its value is not already cached.
### Aliases: cache
### Keywords: manip array

### ** Examples

    bigCalc <- function() runif(10)
    cache(myComplicatedObject <- bigCalc())
    aCopy <- myComplicatedObject
    remove(myComplicatedObject)
    cache(myComplicatedObject <- bigCalc())
    stopifnot(all.equal(myComplicatedObject, aCopy))
    allCacheFiles <- list.files(".", pattern="^tmp_R_cache_.*\.RData$",
                                full.name=TRUE)
    file.remove(allCacheFiles)



