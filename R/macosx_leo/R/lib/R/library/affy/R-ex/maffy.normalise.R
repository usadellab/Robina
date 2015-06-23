### Name: maffy.normalize
### Title: Normalize Itensities
### Aliases: maffy.normalize normalize.contrast
### Keywords: internal

### ** Examples

     data(affybatch.example)
     x <- pm(affybatch.example)[1:2000,1:3]
     mva.pairs(x)
     x <- maffy.normalize(x,subset=1:nrow(x))
     mva.pairs(x)



