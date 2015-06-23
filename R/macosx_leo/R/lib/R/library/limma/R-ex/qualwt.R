### Name: QualityWeights
### Title: Spot Quality Weights
### Aliases: QualityWeights wtarea wtflags wtIgnore.Filter
### Keywords: regression

### ** Examples

#  Read in spot output files from current directory and give full weight to 165
#  pixel spots.  Note: for this example to run you must set fnames to the names
#  of actual spot output files (data not provided).
## Not run: 
##D RG <- read.maimages(fnames,source="spot",wt.fun=wtarea(165))
##D #  Spot will be downweighted according to weights found in RG
##D MA <- normalizeWithinArrays(RG,layout)
## End(Not run)



