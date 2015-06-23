# generate a synthetic "chip" containing
# the median expression values for each
# gdata.norm$Eene across all chips in an expression set

aver.raw <- as.matrix(apply(log2(data$E), 1, median), nrow=1)
aver.norm <- as.matrix(apply(data.norm$E, 1, median), nrow=1)
rownames(aver.norm) <- rownames(data.norm$E)
rownames(aver.raw) <- rownames(data$E)

warnings <- FALSE
bad <- character(0)

for (i in 1:ncol(data.norm$E)) {

	outfile <- paste(tempRoot, "_sc_maplot_", i, ".png", sep="")
	png(file=outfile, height=500, width=1000)
	par(mfrow=c(1,2))

	A.norm <- 0.5*(aver.norm+data.norm$E[,i])
	M.norm <- data.norm$E[,i]-aver.norm

	A.raw <- 0.5*(aver.raw+log2(data$E[,i]))
	M.raw <- log2(data$E[,i])-aver.raw

	samplename <- gsub(	"(\\.txt|\\.cut|\\.tab)",
						"",
						basename(colnames(data$E)[i]),
						perl=T,
						ignore.case=T)

	plotMAlowess(M.raw, A.raw,
				 smooth=0.2,
				 main=samplename,
                                 array=samplename)

	bad <- c(bad, plotMAlowess(M.norm, A.norm,
				 stats=T,
				 smooth=0.2,
				 main=paste(samplename, "normalized"),
                                 array=samplename) )

	dev.off()
}

if (length(bad) > 0) {
	print ("__WARNINGS__", quote=F)
    print ("TYPE:MAplot signal intensity", quote=F)
    print ("SEVERITY:1", quote=F)
    print ("The samples listed below show strong deviations", quote=F)
    print ("from expected values. Exclusion of these samples", quote=F)
    print ("from the analysis should be considered.", quote=F)
    print ("", quote=F)
    print (strwrap(bad), quote=F)
    print ("__WARNINGS_END__", quote=F)
}

