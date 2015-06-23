###
# generic header for reading two color
# microarray data usng limma functions
###

setwd("__OUTPUT_DIR__")

library(limma)
source("source/lib/ellipse.R")
source("source/lib/malowess.R")
source("source/lib/plotSigDensities.R")

## if i don't set the locale to this value i get an
## input string is invalid in this locale later
## when trying to read the generic type data - found
## this as a solution to a similar problem in
## https://stat.ethz.ch/pipermail/bioc-devel/2006-March/000419.html
Sys.setlocale("LC_ALL","C")

## read the targets file

source      <- "__SOURCE_TYPE__"
targetsFile <- "__TARGETS_FILE__"
RG          <- character(0)
tempRoot    <- "__TEMP_ROOT__"
columns <- list(__COLUMNS__)
printLayout <- list(__PRINT_LAYOUT__)
AGILENT_FIX <- FALSE

targets <- readTargets(file=targetsFile)

## not generic type  - we should be able to read most information from the data files
if (source != "generic") {
    
    RG <- read.maimages(files=targets$FileName, source=source, quote="\"'", columns=columns)

} else {
    ## generic type - we need detailed info about the input data
    
    anno <- list("__IDENTIFIER__")    
    RG <- read.maimages(files=targets$FileName, 
                        source="generic", 
                        columns=columns, 
                        annotation=anno,
                        quote="\"'")
    RG$printer <- printLayout
}


# fix incomplete Agilent layout
if ( (is.null(RG$printer)) ||
     (source == 'agilent') && (nrow(RG$genes) != RG$printer$ngrid.c * RG$printer$ngrid.r * RG$printer$nspot.c * RG$printer$nspot.r) ) {

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
    r <- RG$genes$Row
    c <- RG$genes$Col
    nr <- max(r)
    nc <- max(c)

    mR <- mG <- mRb <- mGb <- matrix(NA, nrow=nr*nc, ncol=ncol(RG$R))
    i <- (r-1)*nc+c # recalculate indices

    mR[i, ] <- RG$R
    colnames(mR) <- colnames(RG$R)
    rownames(mR)[i] <- RG$genes$ProbeName
    RG$R <- mR

    mG[i, ] <- RG$G
    colnames(mG) <- colnames(RG$G)
    rownames(mG)[i] <- RG$genes$ProbeName
    RG$G <- mG

    mRb[i, ] <- RG$Rb
    colnames(mRb) <- colnames(RG$Rb)
    rownames(mRb)[i] <- RG$genes$ProbeName
    RG$Rb <- mRb

    mGb[i, ] <- RG$Gb
    colnames(mGb) <- colnames(RG$Gb)
    rownames(mGb)[i] <- RG$genes$ProbeName
    RG$Gb <- mGb

    #fix printer
    RG$printer <- printLayout #list(ngrid.r = 1, ngrid.c = 1, nspot.r = nr, nspot.c = nc)
    AGILENT_FIX <- TRUE
}


########################################
# Check whether all columns are 
# NUMERIC - this will eventually
# introduce NAs 
#

if ( (!is.numeric(RG$R)) ||
     (!is.numeric(RG$Rb)) ||
     (!is.numeric(RG$G)) ||
     (!is.numeric(RG$Gb)) 	) {
	 	
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
	 	
    RG.col.names <- colnames(RG)
    RG$R <- as.matrix(as.double(RG$R))
    RG$G <- as.matrix(as.double(RG$G))
    RG$Rb <- as.matrix(as.double(RG$Rb))
    RG$Gb <- as.matrix(as.double(RG$Gb))
    colnames(RG) <- RG.col.names
}
