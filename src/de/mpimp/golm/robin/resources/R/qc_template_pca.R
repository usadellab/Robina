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

## create a PCA plot
pcalist <- __PC_PLOT__LIST__
rows <- ceiling((length( pcalist )+1)/2)
png(file = paste("__PARAM_OUTPUTFILE__", "pca", ".png", sep=""), width=12, height=6 * rows, units="in", res=120)
plotPCA.mod(eset, col=colors, screeplot=T, pc=pcalist)
dev.off()

## do hierarchical clustering on the data and create a plot
if (length(data) > 2) {
    png(file = paste("__PARAM_OUTPUTFILE__", "hclust", ".png", sep=""), __PARAM_JPEG_PARAMS__)
    plot(hclust(as.dist(1-cor(exprs(eset), method="pearson")), method="complete"))
    dev.off()    
}