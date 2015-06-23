## normalize data for GC content bias effects
## using the methods implemented in the EDASeq 
## package


## assuming the data has been loaded in a dataframe called raw
## as in the rnaseq_header.R script and he working directory was
## set properly

library(EDASeq)

GC_NORMALIZATION <-  TRUE
GC_NORM_METHOD <-  "__GC_NORM_METHOD__"


if (file.exists("input/gene.lengths")) {

        ## generate the proper data structure for EDASeq
	f <- read.table(file="input/gene.lengths", header=F, sep="\t", as.is=T, row.names=1)
	common <- intersect(rownames(raw), rownames(f))
	gc_length <- cbind(f[common,2:1])

	colnames(gc_length) <- c("gc", "length")

	data <- newSeqExpressionSet(
		exprs=as.matrix(raw),
		featureData=as.data.frame(gc_length),
		phenoData=data.frame(conditions=as.vector(groups), row.names=colnames(raw))
	)

	## visualize GC bias effect
	## biasPlot(data,"gc",log=T, ylim=c(0,2))


	## normalize GC bias in data within and between lanes
	dataWithin <- withinLaneNormalization(data,"gc",which=unlist(strsplit(GC_NORM_METHOD, ";"))[1]) ## loess, median, upper or full
	dataNorm <- betweenLaneNormalization(dataWithin,which=unlist(strsplit(GC_NORM_METHOD, ";"))[2]) ## median, upper or full

	## eliminate Inf, NaN and NA from the counts table - all set to zero
	exprs(dataNorm)[which(is.infinite(exprs(dataNorm)))] <- 0
	exprs(dataNorm)[which(is.na(exprs(dataNorm)))] <- 0
	exprs(dataNorm)[which(is.nan(exprs(dataNorm)))] <- 0

	percent.zero.datapoints <- (length(which(exprs(dataNorm) == 0)) / length(exprs(dataNorm))) * 100
	percent.zero.datapoints.raw <- (length(which(raw == 0)) / length(as.matrix(raw))) * 100

	## stop if more than 80% of the data points are zero... is this a good threshold?
	if (percent.zero.datapoints > 80) {
		print(paste("WARNING:  ", percent.zero.datapoints, "% of the counts are zero."))
		print(      "This might have happend during the GC content")
		print(      "bias normalization (depending on the methods chosen).")
		print(paste("Percentage of zero data points in the raw counts table is", percent.zero.datapoints.raw, "%."))	
		print(      "Stopping the analysis.")
		stop("more than 80% zero counts after normalization")
	}

	raw <- exprs(dataNorm)
}