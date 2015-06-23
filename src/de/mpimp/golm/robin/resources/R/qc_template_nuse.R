## fit probe level models to the data (if it has not been done before)
library(affyPLM)
if (!exists("plmset")) {
    plmset <- fitPLM(data)
}

if (length(data$sample) > 1) {

    ## plot the normalized unscaled standard errors
    png(file = paste("__PARAM_OUTPUTFILE__", "nuse", ".png", sep=""), width=6, height=6+longestName*0.075, units="in", res=120)
    par(cex=0.75, mar=c(longestName*0.6, 4, 4, 2))

    # calc scale for the plot to display all data properly
    nuse <- NUSE(plmset, type="values")
    ylimits <- c(min(nuse[,1:ncol(nuse)], na.rm=T), max(nuse[,1:ncol(nuse)], na.rm=T))

    if (any(is.infinite(ylimits))) {
        NUSE(plmset, las=2, col=colors, main=paste("NUSE Plot of",length(PARAM_INPUTFILES),"Affymetrix files"))
    } else {
        NUSE(plmset, las=2, col=colors, main=paste("NUSE Plot of",length(PARAM_INPUTFILES),"Affymetrix files"), ylim=ylimits)
    }
    dev.off()

    ## plot the relative logarithmic expression
    png(file = paste("__PARAM_OUTPUTFILE__", "rle", ".png", sep=""), width=6, height=6+longestName*0.075, units="in", res=120)
    par(cex=0.75, mar=c(longestName*0.6, 4, 4, 2))

    # calc scale for the plot to display all data properly
    rle <- RLE(plmset, type="values")
    rle.ylimits <- c(min(rle[,1:ncol(rle)], na.rm=T), max(rle[,1:ncol(rle)], na.rm=T))

    if (any(is.infinite(rle.ylimits))) {
        RLE(plmset, las=2, col=colors, main=paste("RLE Plot of",length(PARAM_INPUTFILES),"Affymetrix files"))
    } else {    
        RLE(plmset, las=2, col=colors, main=paste("RLE Plot of",length(PARAM_INPUTFILES),"Affymetrix files"), ylim=rle.ylimits)
    }
    dev.off()
}
