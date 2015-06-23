### Name: mergeScans
### Title: Merge two scans of two-color arrays
### Aliases: mergeScans mergeScansRG
### Keywords: models

### ** Examples

## Not run: 
##D #RG1: An RGList from low scan
##D #RG2: An RGList from high scan
##D RGmerged <- mergeScansRG(RG1,RG2,AboveNoiseLowG=ANc3,AboveNoiseLowR=ANc5)
##D 
##D #merge two scans when all spots are above noise in low scan and no outlier detection.
##D RGmerged <- mergeScansRG(RG1,RG2,outlierp=0)
## End(Not run)



