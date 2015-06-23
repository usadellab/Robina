
## plot signal intensity distribution for each channel on each chip
source("source/lib/plotSigDensities.R")
source("source/lib/RGbox.R")

outfile <- paste(tempRoot, "_density", ".png", sep="")
png(file=outfile, height=1000, width=1000)
par(mfrow=c(2,2))

## unnormalized
plotSigDensities(RG, main="Unnormalized")

normWithin <- __NORM_WITHIN__
normBetween <- __NORM_BETWEEN__

MA.norm <- character(0)

## normalize the data
if (normWithin) {
    MA.norm <- normalizeWithinArrays(RG, method="__NORM_WITHIN_METHOD__", bc.method="__BGCORR_METHOD__")
}
if (normBetween) {
    MA.norm <- normalizeBetweenArrays(MA.norm, method="__NORM_BETWEEN_METHOD__")
}

plotSigDensities(MA.norm, main="Normalized: __NORM_WITHIN_METHOD__, __BGCORR_METHOD__, __NORM_BETWEEN_METHOD__")
RGbox(RG, main="Raw intensity distribution")
RGbox(MA.norm, main="Normalized")
dev.off()