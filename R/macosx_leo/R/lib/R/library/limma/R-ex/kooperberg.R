### Name: kooperberg
### Title: Kooperberg Model-Based Background Correction
### Aliases: kooperberg
### Keywords: models

### ** Examples

#  This is example code for reading and background correcting GenePix data
#  given GenePix Results (gpr) files in the working directory (data not
#  provided).
## Not run: 
##D genepixFiles <- dir(pattern="*\\.gpr$") # get the names of the GenePix image analysis output files in the current directory
##D RG <- read.maimages(genepixFiles, source="genepix", other.columns=c("F635 SD","B635 SD","F532 SD","B532 SD","B532 Mean","B635 Mean","F Pixels","B Pixels"))
##D RGmodel <- kooperberg(RG)
##D MA <- normalizeWithinArrays(RGmodel)
## End(Not run)



