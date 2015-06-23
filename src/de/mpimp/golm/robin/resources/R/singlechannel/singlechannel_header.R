################################
# common header template for
# single channel QC analyses
################################

library(limma)

setwd("__OUTPUT_DIR__")

source("source/lib/plotSigDensities.R")
source("source/lib/malowess.R")
source("source/lib/ellipse.R")

## if i don't set the locale to this value i get an
## input string is invalid in this locale later
## when trying to read the generic type data - found
## this as a solution to a similar problem in
## https://stat.ethz.ch/pipermail/bioc-devel/2006-March/000419.html
Sys.setlocale("LC_ALL","C")

## read the targets file

tempRoot    <- "__TEMP_ROOT__"
AGILENT_FIX <- FALSE

PARAM_INPUTFILES <- c(
__PARAM_FILES__
)

PARAM_FILENAMES <- basename(PARAM_INPUTFILES);

longestName <- nchar(max(PARAM_FILENAMES))
colors <- rainbow(length(PARAM_INPUTFILES))
columns <- list(__COLUMNS__)
anno <- list("__IDENTIFIER__")
printLayout <- list(__PRINT_LAYOUT__)
source <- "__SOURCE_TYPE__"


if (source != 'generic') {
    data <- read.maimages(files=PARAM_INPUTFILES, 
                            source=source, 
                            quote="\"'", 
                            #channels=1, 
                            columns=columns)
} else {
    data <- read.maimages(  files=PARAM_INPUTFILES,
                        source="generic",
                        columns=columns,
                        annotation=anno,
                        #channels=1,
                        quote="\"'")

    data$printer <- printLayout
}

if (source == 'agilent') {
	rownames(data$E) <- data$genes$ProbeName
	rownames(data$Eb) <- data$genes$ProbeName
} else {
	rownames(data$E) <- data$genes[,1]
	rownames(data$Eb) <- data$genes[,1]
}


# fix incomplete Agilent layout
if ( (is.null(data$printer)) ||
     (source == 'agilent') && (nrow(data$genes) != data$printer$ngrid.c * data$printer$ngrid.r * data$printer$nspot.c * data$printer$nspot.r) ) {

    print ("__WARNINGS__", quote=F)
    print ("TYPE:Input data inconsistency", quote=F)
    print ("SEVERITY:2", quote=F)
    print ("The input data contains less data rows than specified in the layout.", quote=F)
    print ("This is a known problem occurring in Agilent data caused by the fact", quote=F)
    print ("that Agilent sometimes exclude spots (e.g. blank spots etc) from the", quote=F)
    print ("output files. Robin will now fix this problem by padding out the input", quote=F)
    print ("data with NAs.", quote=F)
    print ("", quote=F)
    print ("__WARNINGS_END__", quote=F)

    # fix taken and modified from a post of Gordon Smyth
    # [BioC] Limma: setting up printer information for agilent
    # Gordon K Smyth smyth at wehi.EDU.AU
    # Thu Jan 4 13:08:08 CET 2007
    r <- data$genes$Row
    c <- data$genes$Col
    nr <- max(r)
    nc <- max(c)

    mE <- mEb <- matrix(NA, nrow=nr*nc, ncol=ncol(data$E))
    i <- (r-1)*nc+c # recalculate indices

    mE[i, ] <- data$E
    colnames(mE) <- colnames(data$E)
    rownames(mE)[i] <- data$genes$ProbeName
    data$E <- mE

    mEb[i, ] <- data$Eb
    colnames(mEb) <- colnames(data$Eb)
    rownames(mEb)[i] <- data$genes$ProbeName

    data$Eb <- mEb

    #fix printer
    data$printer <- list(ngrid.r = 1, ngrid.c = 1, nspot.r = nr, nspot.c = nc)
    AGILENT_FIX <- TRUE
}



# normalization
normBetween <- __NORM_BETWEEN__
bgCorr <- __BACKGROUND_CORRECT__

data.norm <- character()

if (bgCorr) {
    data.norm <- backgroundCorrect(data, method="__BGCORR_METHOD__")
}

if (normBetween) {
    data.norm$E <- normalizeBetweenArrays(data.norm$E, method="__NORM_BETWEEN_METHOD__")
}


# transform to log2 scale
data.norm$E <- log2(data.norm$E)


########################################
# Check whether all columns are
# NUMERIC - this will eventually
# introduce NAs
#

if (!is.numeric(data$E) || !is.numeric(data$Eb)) {

    print ("__WARNINGS__", quote=F)
    print ("TYPE:Input data inconsistency", quote=F)
    print ("SEVERITY:2", quote=F)
    print ("The input data contains non-numeric values in columns", quote=F)
    print ("that should only contain numeric values. Robin will", quote=F)
    print ("coerce all values to numeric format eventually introducing", quote=F)
    print ("NAs - this might break/interfere with downstream analyses.", quote=F)
    print ("We strongly recommend that the input data be checked and", quote=F)
    print ("cleaned up. We are not taking any responsibility for the", quote=F)
    print ("soundness of results generated from inconsistend data.", quote=F)
    print ("", quote=F)
    print ("__WARNINGS_END__", quote=F)

    data.col.names <- colnames(data)
    data$E <- as.matrix(as.double(data$E))
    data$Eb <- as.matrix(as.double(data$Eb))
    colnames(data) <- data.col.names
}

