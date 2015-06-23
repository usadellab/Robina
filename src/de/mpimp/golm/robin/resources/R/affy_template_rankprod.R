#################################################
# Template script for the rank product based
# analysis of affymetrix microarray data
# using the R package RankProd
#################################################

library(affy)
library(limma)
library(gcrma) ## this should only be included if the gcrma option was actually chosen!
library(plier)
#library(trma)
library(RankProd)

FILENAMES 	<- c(
__PARAM_FILENAMES__)

CLASSES 	<- c(
__PARAM_CLASSES__)

ORIGIN		<- c(
__PARAM_ORIGIN__)

setwd("__PARAM_OUTPATH__")


# now read in the data and normalize it
data <- ReadAffy(filenames=FILENAMES)

# import of a CDF file required?
if (__IMPORT_CDF_FILE__) {
    library(makecdfenv)
    IMPORTCDF <- make.cdf.env(filename="__CDF_FILE_NAME__", cdf.path="__CDF_FILE_PATH__", compress = FALSE,
             return.env.only = TRUE,
             verbose = FALSE )

    data@cdfName <- "IMPORTCDF"
}

if ("__PARAM_NORM_METHOD__" == "justPlier") {
    eset <- justPlier(data, normalize=T, norm.type="together")
} else {
    eset <- __PARAM_NORM_METHOD__(data)
}

# mas5 normalized expression estimates need to be log transformed
if ("__PARAM_NORM_METHOD__" == "mas5") {
    exprs(eset) <- log2(exprs(eset))
}

data <- exprs(eset)

RP.out <- RPadvance(data, CLASSES, ORIGIN, 
			num.perm=100, 
			na.rm=F,
			logged=T,
			gene.names=rownames(data),
			plot=F )

# PVALUE CORRECTION 
RP.out$pval <- apply(RP.out$pval, 2, p.adjust, method="__PVALMETHOD__")
	
png(file="plots/RankProd_plot.png")		
plotRP(RP.out, cutoff= 0.05)
dev.off()

top100 <- topGene(RP.out, 
		num.gene=100, 
		method="pfp", 
		logged=T, 
		logbase=2, 
		gene.names=rownames(data) )
		
		
header1 <- c("Identifier", colnames(top100$Table1))
write.table(as.list(header1), file="detailed_results/__EXPNAME___top100_upregulated.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(top100$Table1, file="detailed_results/__EXPNAME___top100_upregulated.txt", sep="\t", quote=F, append=T, col.names=F)

header2 <- c("Identifier", colnames(top100$Table2))
write.table(as.list(header2), file="detailed_results/__EXPNAME___top100_downregulated.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(top100$Table2, file="detailed_results/__EXPNAME___top100_downregulated.txt", sep="\t", quote=F, append=T, col.names=F)

write.table(RP.out, file="__EXPNAME___results.txt", sep="\t", quote=F)

headerMapMan <- c("Identifier", "aver_fold_change", "d_pval_up", "d_pval_down", "d_RPrank_up", "d_RPrank_down")
write.table(as.list(headerMapMan), file="detailed_results/__EXPNAME___results_short.txt", sep="\t", quote=F, row.names=F, col.names=F)
shortOut <- cbind(RP.out$AveFC, RP.out$pval, RP.out$RPrank)
write.table(shortOut, file="detailed_results/__EXPNAME___results_short.txt", sep="\t", quote=F, append=T, col.names=F)

		
