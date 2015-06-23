################################
# common header template for
# all QC analyses
################################

library(affy)
library(limma)
library(gcrma) ## this should only be included if the gcrma option was actually chosen!
library(plier)
#library(trma)

setwd("__PROJECT_DIR__")

source("source/lib/modifiedAffyCoreTools.R")

PARAM_INPUTFILES <- c(
__PARAM_FILES__
)

PARAM_FILENAMES <- c(
__PARAM_FILENAMES__
)

longestName <- nchar(max(PARAM_FILENAMES))

# read and normalize the data
data <- ReadAffy(filenames=PARAM_INPUTFILES)


# import of a CDF file required?
if (__IMPORT_CDF_FILE__) {
    library(makecdfenv)
    IMPORTCDF <- make.cdf.env(filename="__CDF_FILE_NAME__", cdf.path="__CDF_FILE_PATH__", compress = FALSE,
             return.env.only = TRUE,
             verbose = FALSE )

    data@cdfName <- "IMPORTCDF"
}

# check PM signal saturation
checkOversaturation <- function(sample) {

	d <- pm(sample)
	e <- d[d==max(d)]
	f <- (length(e) / length(d)) * 100
	n <- colnames(d)

	if (f > 0.25) {
		print("__WARNINGS__", quote=F)
      	print ("TYPE:Perfect match probe intensity oversaturation", quote=F)
      	print ("SEVERITY:2", quote=F)
      	print(	paste(	"Sample",
                        n,
			"contains more than 0.25% saturated"), quote=F)

		print(	paste(	"probes. (",
				length(e),
				"/",
				length(d),
				" PM probes, ",
				"max intensity: ",
				max(d),
				")", sep=""), quote=F)
      	#print("", quote=F)
      	print("__WARNINGS_END__", quote=F)
     }
}

for (i in 1:length(data)) {
	checkOversaturation(data[,i])
}

sampleNames(data) <- c(PARAM_FILENAMES)
colors <- rainbow(length(PARAM_INPUTFILES))

## somewhere here we either have to copy the modifiedCoreTools functions
## or (more elegantly) source them in from a file...

