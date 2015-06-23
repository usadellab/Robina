## create boxplots
png(file = paste("__PARAM_OUTPUTFILE__", "boxplot", ".png", sep=""), width=6, height=6+longestName*0.075, units="in", res=120)
par(cex=0.75, mar=c(longestName*0.6, 4, 4, 2))
boxplot(data, las=2, col=colors, main=paste("Boxplot of", length(PARAM_INPUTFILES), "input files"))
dev.off()

## now create MA plots of the normalized data
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

# generate a synthetic "chip" containing
# the median expression values for each
# gene across all chips in an expression set
#
# eset is a normalized expression set
# as generated from an AffyBatch object
# by applying rma() to it

aver <- as.matrix(apply(pset, 1, median), nrow=1)
rownames(aver) <- rownames(pset)
warnings <- FALSE
badsamples <- c()
# now do a MA plot for each individual
# chip against the synthetic chip

for (i in 1:length(data$sample)) {		
        png(file = paste("__PARAM_OUTPUTFILE__", "maplot", i, ".png", sep=""), __PARAM_JPEG_PARAMS__)
	
	# MA plot
	A <- 0.5*(aver+pset[,i])
	M <- pset[,i]-aver
	plot(A, M, pch=".", main=sampleNames(data)[i])
	abline(h=0, col="blue")

	# with lowess fit curve
	lowess.fit <- lowess(A, M)
	lines(lowess.fit, col="red")

	# calculate the integral of the lowess fit over
	# the zero line (absolute values) to assess 
	# chip quality
	lx <- lowess.fit$x[-1]
	integral <- sum(abs(lowess.fit$y[-1])*(lx-lowess.fit$x[1:(length(lowess.fit$x)-1)]))
	
	# check percentage of genes above lfc 1
	lfc <- M[M>=1]
	perc <- round((length(lfc) / length(M)) * 100, 3)	
	
	abline(h=1, col="gray50", lty="dotted")
	abline(h=-1, col="gray50", , lty="dotted")	
	
	textcol <- "black"
	# check the values and issue warnings
	if ((integral > 1.5) || (perc > 5)) {
		warnings <- TRUE
		badsamples <- c(badsamples, sampleNames(data)[i])
		textcol <- "red"
	} 
	
	linespc = (max(M)-min(M)) / 25
	textX = min(A)+0.25
	textY = max(M)-linespc
	
	par(cex=0.75)
	text(textX, textY, paste("I =",as.character(round(integral,3))), adj=0, col=textcol)
	text(textX, textY-linespc, paste("%>LFC1 =",as.character(perc)),  adj=0, col=textcol )
	text(textX, textY-linespc*2, paste("Median =",as.character(round(median(M), 3))), adj=0 )
	dev.off()
}

if (warnings) {
		print ("__WARNINGS__", quote=F)
		print ("TYPE:MAplot signal intensity", quote=F)
		print ("SEVERITY:1", quote=F)
		print ("The samples listed below show strong deviations", quote=F)
		print ("from expected values. Exclusion of these samples", quote=F)
		print ("from the analysis should be considered.", quote=F)
		print ("", quote=F)
		print (strwrap(badsamples), quote=F)
		print ("__WARNINGS_END__", quote=F)
}

