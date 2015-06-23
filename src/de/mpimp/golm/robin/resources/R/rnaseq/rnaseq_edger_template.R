## template for DESeq-based analysis
library(edgeR)

source("source/lib/ellipse.R")
source("source/lib/robinVennDiagram.R")
source("source/lib/malowess.R")
source("source/lib/robinPlotMDS.dge.R")

###################################
# settings to be completed by GUI #
###################################

P_VAL_ADJUST_METHOD <- "__PVALMETHOD__"
P_VAL_CUTOFF        <- __PARAM_PVALCUTOFF__
MIN_LFC2_ONE        <- __MIN_LOGFOLDCHANGE_2_WANTED__
DISPERSION          <- "__DISPERSION__"

design <- model.matrix(~ -1+groups)
colnames(design) <- as.character(unique(groups))

d <- DGEList(counts=raw, group=groups)
d <- calcNormFactors(d)

if (all(tabulate(groups) <= 1)) {
	## there is no replication whatsoever. We should actually
	## stop the script here and ask the user what he/she wants to
	## do. To keep the analysis running we set the common disp.
	## to a rel. high value
	d$common.dispersion <- 0.4	
} else {
	d <- estimateCommonDisp(d)
}

if (DISPERSION == "tagwise") {

        # the prior n governs the strength of the "squeezing" applied to the 
        # tagwise dispersions towards the common dispersion value. Higher
        # prior n will result in tagwise dispersion values closer to the
        # common disp. value. As a rule of thumb the value can be chosen as
        # 50 / (#samples - #groups)

        pr.N <- round( 50 / (length(groups) - length(levels(groups))))

	d <- estimateTagwiseDisp(d, prior.n = pr.N) 
	png(file = "plots/tagwise_disp_vs_logconc.png", height = 600, width = 600)
	plot(log(d$conc$conc.common), d$tagwise.dispersion, panel.first = grid(), ylab = "tagwise dispersion", xlab = "logConc")
	abline(h = d$common.dispersion, col = "dodgerblue", lwd = 3) 
	dev.off()
} 

## define pairwise contrasts
contrast.table <- __CONTRAST_TABLE__

result <- c()
res.cols <- character(0)
						
for (i in 1:nrow(contrast.table)) {
	cond1 <- contrast.table[i, 1]
	cond2 <- contrast.table[i, 2]
	contrast <- paste(cond1, "-", cond2, sep="")	
	print(paste("computing contrast", contrast))
	
	
	
	de <- exactTest(d, dispersion=DISPERSION, pair=rev(contrast.table[i,]))
	
	if (FALSE %in% (rownames(result) == rownames(de$table))) {
		stop("data table rownames inconsistent")		
	}
	
	top <- topTags(de, n=nrow(de), adjust.method=P_VAL_ADJUST_METHOD)	
	## write the detailed result for each contrast to file
	write.table(as.list(c("Identifier", colnames(top$table))), file=paste("detailed_results/full_table_", contrast, ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F,
				col.names=F)
	write.table(top$table, file=paste("detailed_results/full_table_", contrast, ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=T,
				col.names=F,
				append = T)

        ## write the significantly changing tags only
	write.table(as.list(c("Identifier", colnames(top$table))), file=paste("detailed_results/significant_", contrast, ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F,
				col.names=F)
	write.table(top$table[which(top$table$FDR < P_VAL_CUTOFF),], file=paste("detailed_results/significant_", contrast, ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=T,
				col.names=F,
				append = T)                      
        
	
	
	dt <- decideTestsDGE(de, adjust.method=P_VAL_ADJUST_METHOD, p.value=P_VAL_CUTOFF)
	result <- cbind(result, de$table$logFC, dt)
	res.cols <- c(res.cols, contrast, paste("d_", contrast, sep="") )
	colnames(result) <- res.cols
	rownames(result) <- rownames(de$table)
	
	sig.tags <- rownames(top$table)[which(top$table$FDR < P_VAL_CUTOFF)]
	png(file=paste(sep="","plots/MAplot_", contrast, ".png"),
        width=600,
        height=600)

        if (nrow(de$table) <= 50) {
            plotSmear(de, de.tags=sig.tags,	main=paste("MA plot of contrast", contrast), lowess=F ) 
        } else {
            plotSmear(de, de.tags=sig.tags,	main=paste("MA plot of contrast", contrast), lowess=T ) 
        }
	dev.off()
}


## Venn diagrams
vennres <- as.matrix(result[, seq(from=2, to=ncol(result), by=2)])
colnames(vennres) <- sub("^d_", "", colnames(result)[seq(from=2, to=ncol(result), by=2)])

if (nrow(contrast.table) <= 4) {
    png(filename="plots/vennDiagram_total.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(vennres, main="Significantly regulated genes")
    dev.off()

    png(filename="plots/vennDiagram_up.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(vennres, main="Upregulated genes", include="up")
    dev.off()

    png(filename="plots/vennDiagram_down.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(vennres, main="Downregulated genes", include="down")
    dev.off()
}


# now write the results for each comparison to file
# the resulting table will have a pair of columns for
# each constrast. The first coumns contains the normalized
# log fold change and the second the result of the significance
# test for each gene: 1=significantly upregulated, 0=not sig.
# -1=sig. downregulated.

header <- c("Identifier")
header <- c(colnames(result))

result.file.name <- paste(PROJECT_NAME, "_results.txt", sep="")

write.table(as.list(header), file=result.file.name, sep="\t", quote=F, row.names=F, col.names=F)
write.table(result, file=result.file.name, sep="\t", quote=F, col.names=F, append=T)

write.short.sessionInfo("source/R.session.info.txt")

tryCatch (
	{
		## do an overview MDS plot
		## but only if there is replication - otherwise the function breaks
		if (length(groups) > length(levels(groups)) ) {
		    png(file="plots/MDSplot.png",
		        width=800,
		        height=600)
		    robinPlotMDS.dge(d, top=1000, main="MDS plot", groups=groups)
                    dev.off()
		}
	},
	error = function (e) {
		print(e)
		if (length(groups) > length(levels(groups)) ) {
		    png(file="plots/MDSplot.png",
		        width=800,
		        height=600)
		    plotMDS(d, top=1000, main="MDS plot")
		    dev.off()
		}
		# if that worked exit with 0
		quit(save="no", status=0)
	} 
)