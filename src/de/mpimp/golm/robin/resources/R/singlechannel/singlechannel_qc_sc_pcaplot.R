##########################################################
## PCA and hierarchical clustering of the normalized data
##########################################################


## PCA
if (ncol(data.norm$E) > 2) {
    png(file=paste(tempRoot, "_sc_pcaplot", ".png", sep=""),
            width=800,
            height=600)

    layout(matrix(1:2, nc = 2), c(3,1))
    	
    m <- na.omit(data.norm$E)
    m[which(is.infinite(m))] <- 0
		
    pca <- prcomp(t(m))
    pca.info <- summary(pca)

    plot(pca$x[,1:2],
            cex=1.5,
            main="Principal component analysis",
            col=1:ncol(data.norm$E),
            pch=1:ncol(data.norm$E),
            xlab=paste("PC1:", round(pca.info$imp[2,1]*100, digits=2), "%"),
            ylab=paste("PC2:", round(pca.info$imp[2,2]*100, digits=2), "%") )

    par(mai = c(0,0,1.01,0))
    plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
    legend(	"topleft",
                    legend=gsub(	"(\\.txt|\\.cut|\\.tab)",
                                                    "",
                                                    basename(colnames(data.norm$E)),
                                                    perl=T,
                                                    ignore.case=T),
                    col=1:ncol(data.norm$E),
                    pch=1:ncol(data.norm$E),
                    bty="n", cex=1.0)
    dev.off()
}