### Name: normalize.loess
### Title: Scale microarray data
### Aliases: normalize.loess normalize.AffyBatch.loess
### Keywords: smooth

### ** Examples

     #require(affy)
     #data(Dilution)
     #x <- pm(Dilution[,1:3])
     #mva.pairs(x)
     #x <- normalize.loess(x,subset=1:nrow(x))
     #mva.pairs(x)



