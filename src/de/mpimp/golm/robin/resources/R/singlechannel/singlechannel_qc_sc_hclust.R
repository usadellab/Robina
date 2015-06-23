## HCLUST

if (ncol(data.norm$E) > 2) {
    png(file=paste(tempRoot, "_sc_hclust", ".png", sep=""),
            width=600,
            height=600)

    colnames(data.norm$E) <- gsub(	"(\\.txt|\\.cut|\\.tab)",
                                                    "",
                                                    basename(colnames(data.norm$E)),
                                                    perl=T,
                                                    ignore.case=T)
    m <- na.omit(data.norm$E)
	m[which(is.infinite(m))] <- 0
	
    hc <- hclust(	as.dist(1-cor(m, method="pearson", )),
                                    method="complete")
    plot(hc)
    dev.off()    
}