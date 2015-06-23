## fit probe level models to the data (if it has not been done before)
library(affyPLM)
if (!exists("plmset")) {
    plmset <- fitPLM(data)
}

## if there's only one sample...
if (length(data$sample) < 2) {
    print ("__WARNINGS__", quote=F)
    print ("TYPE: PLM", quote=F)
    print ("SEVERITY:1", quote=F)
    print ("Visualizing probe level model residuals when", quote=F)
    print ("using only one input file does not give informative", quote=F)
    print ("results. Showing a pseudo image of the the raw signal", quote=F)
    print ("intensities instead.", quote=F)
    print (PARAM_INPUTFILES, quote=F)
    print ("__WARNINGS_END__", quote=F)
    png(file = paste("__PARAM_OUTPUTFILE__", "plm", "1", ".png", sep=""), __PARAM_JPEG_PARAMS__)    
    par(mar=c(0.5,0.5,2,0.5))
    image(data)
    dev.off()
} else {
    for (i in 1:length(data)) {
        png(file = paste("__PARAM_OUTPUTFILE__", "plm", i, ".png", sep=""), __PARAM_JPEG_PARAMS__)        
        par(mar=c(0.5,0.5,2,0.5))
        image(plmset, which = i)
        dev.off()
    }
}

