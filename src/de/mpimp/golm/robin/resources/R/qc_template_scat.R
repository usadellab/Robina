## normalize the data 
if (!exists("eset")) {    
    if ("__PARAM_NORM_METHOD__" == "justPlier") {
        eset <- justPlier(data, normalize=T, norm.type="together")
    } else {
        eset <- __PARAM_NORM_METHOD__(data)
    }
}

# mas5 normalized expression estimates need to be log transformed
if ("__PARAM_NORM_METHOD__" == "mas5") {
    exprs(eset) <- log2(exprs(eset))
} 

## extract the expression values
if (!exists("pset")) {
    pset <- exprs(eset)
}

## create scatter plots of all sample combinations
for (i in 1:length(data$sample)) {
    for (j in 1:length(data$sample)) {
        if (j>i) {
            png(file = paste("__PARAM_OUTPUTFILE__", "scat", i, j, ".png", sep=""), __PARAM_JPEG_PARAMS__)  
            plot(pset[,i], pset[,j], xlab = sampleNames(data)[i], ylab = sampleNames(data)[j])
            abline(0,1, col="blue")
            abline(1,1, col="red")
            abline(-1,1, col="red")
            dev.off()
        }    
    }
}

