## template for limma-based analysis of
## non-affy single channel microarray
## experiments

source("source/lib/ellipse.R")
source("source/lib/malowess.R")
source("source/lib/robinVennDiagram.R")

groups <- c(

    # list of character vectors
    # giving the group names
    # this list has to correspond
    # to the sequence of input files

    __PARAM_GROUPS__

)

# now build the design model matrix
samples <- as.factor(c(

    # for each group assign a coefficient
    # starting from 1 and repeat it
    # for each file(sample) of the resp.
    # group. e.g. 3 groups, 3,5,4 files
    # makes c(rep(1,3), rep(2,5), rep(3,4))

    __PARAM_MODEL__

))

# create the model matrix and assign the group names
design <- model.matrix(~ -1+samples)
colnames(design) <- groups

# now we can create the contrast matrix describing
# the comparisons between the sample groups
contrast.matrix <- makeContrasts(

    # list of
    # COMPx=GROUPa-GROUPb
    # or DIFFGabGcd=(GROUPa-GROUPb)-(GROUPc-GROUPd)
    # terms describing which groups are
    # to be compared
    __CONTRAST_TERMS__
    , levels=design
)


## limma statistics...

fit <- lmFit(data.norm$E, design)
fit2 <- contrasts.fit(fit, contrast.matrix)
fit2 <- eBayes(fit2)

# test all hypotheses in the contrast matrix
results <- decideTests(fit2, method="__DOWN_WEIGH_PROCEDURE__",
                             adjust.method="__PVALMETHOD__",
                             lfc=__MIN_LOGFOLDCHANGE_2_WANTED__,
                             p.value=__PARAM_PVALCUTOFF__)

# check whether there a any significantly regulated
# genes after the multiple testing

# produce a warning if true
if (length(results[results!=0]) == 0) {
    print("__WARNINGS__", quote=F)
    print ("TYPE:Significance test", quote=F)
    print ("SEVERITY:2", quote=F)
    print("According to the results of the multiple testing", quote=F)
    print("there are no significantly regulated genes in the", quote=F)
    print("analysis. Please check your analysis design to make", quote=F)
    print("sure this was not caused by a mistake in e.g. the", quote=F)
    print("assignment of the input file groups.", quote=F)
    print("__WARNINGS_END__", quote=F)
}




if (__WRITE_RAW_EXPRS__) {
    # write the raw normalized expression values to file
    samplesHeader <- as.list(c("Identifier", basename(colnames(data.norm$E))))

    write.table(samplesHeader, sep="\t", col.names=FALSE, row.names=FALSE, file="detailed_results/raw_normalized_expression_values.txt", quote=FALSE)
    write.table(data.norm$E,file="detailed_results/raw_normalized_expression_values.txt",sep="\t",quote=F, col.names=FALSE, append=TRUE)
}

# and draw venn diagrams showing the number of
# genes reacting significantly in each comparison
# ONLY IF 4 AND LESS CONTRASTS (using the extended Venn code)
if (ncol(contrast.matrix) <= 4) {
    png(filename="plots/vennDiagram_total.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, main="Significantly regulated genes")
    dev.off()

    png(filename="plots/vennDiagram_up.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, main="Upregulated genes", include="up")
    dev.off()

    png(filename="plots/vennDiagram_down.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, main="Downregulated genes", include="down")
    dev.off()
}

# create MA plots for all contrasts
# showing significant points highlighted
for (i in 1:ncol(fit2$coef)) {
    png(file=paste(sep="","plots/MAplot_", colnames(fit2$coef)[i], ".png"),
        width=600,
        height=600)
    signif <- abs(results[,i])
	
	A <- apply(data.norm$E, 1, mean)
	
	plotMAlowess(fit2$coef[, i],
				 A,
				 smooth=0.2,
				 main=paste(	sep=" ",
                        "MA Plot of contrast",
                        colnames(fit2$coef)[i]),
                 array="",
                 sig=signif)
    dev.off()
}


## group PCA plot with ellipse around clustering points

m <- na.omit(data.norm$E)
m[which(is.infinite(m))] <- 0
		
pca <- prcomp(t(m))

# plot principal components 1 and 2
png(file=paste(sep="","plots/PCAplot_", ".png"),
        width=800,
        height=600)

layout(matrix(1:2, nc = 2), c(3,1))
pca.info <- summary(pca)

plot(pca$x[,1:2],
	main="Principal component analysis",
	col="white",
	xlab=paste("PC1:", round(pca.info$imp[2,1]*100, digits=2), "%"),
	ylab=paste("PC2:", round(pca.info$imp[2,2]*100, digits=2), "%") )

for (i in 1:length(groups)) {

	points(pca$x[samples==i, 1], pca$x[samples==i, 2], col=i, pch=i )

	center.x <- mean(pca$x[samples==i, 1])
	center.y <- mean(pca$x[samples==i, 2])

	a <- (max(pca$x[samples==i, 1]) - min(pca$x[samples==i, 1]))
	b <- (max(pca$x[samples==i, 2]) - min(pca$x[samples==i, 2]))

	ellipse(center=c(center.x, center.y), radius=c(a,b), rotate=0, add=T, col=i, lty="dotted")
}
par(mai = c(0,0,1.01,0))
plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
legend("topleft", legend=groups, col=c(1:length(groups)), pch=c(1:length(groups)), bty="n", cex=0.75)
dev.off()

## plot again as PDF
pdf(file=paste(sep="","plots/PCAplot", ".pdf"), width=8, height=6)
layout(matrix(1:2, nc = 2), c(3,1))
pca.info <- summary(pca)

plot(pca$x[,1:2],
	main="Principal component analysis",
	col="white",
	xlab=paste("PC1:", round(pca.info$imp[2,1]*100, digits=2), "%"),
	ylab=paste("PC2:", round(pca.info$imp[2,2]*100, digits=2), "%") )


for (i in 1:length(groups)) {

	points(pca$x[samples==i, 1], pca$x[samples==i, 2], col=i, pch=i )

	center.x <- mean(pca$x[samples==i, 1])
	center.y <- mean(pca$x[samples==i, 2])

	a <- (max(pca$x[samples==i, 1]) - min(pca$x[samples==i, 1]))
	b <- (max(pca$x[samples==i, 2]) - min(pca$x[samples==i, 2]))

	ellipse(center=c(center.x, center.y), radius=c(a,b), rotate=0, add=T, col=i, lty="dotted")
}
par(mai = c(0,0,1.01,0))
plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
legend("topleft", legend=groups, col=c(1:length(groups)), pch=c(1:length(groups)), bty="n", cex=0.75)
dev.off()

# now write the results for each comparison to file
# the resulting table will have a pair of columns for
# each constrast. The first coumns contains the normalized
# log fold change and the second the result of the significance
# test for each gene: 1=significantly upregulated, 0=not sig.
# -1=sig. downregulated.

header <- c("Identifier")

df <- character(0)

for (i in 1:ncol(fit2)) {
	if (ncol(fit2)==1) {
		df <- cbind(fit2$coef, results[,i])
	} else {
		df <- cbind(df, fit2$coef[,i], results[,i])
	}
	header <- c(header, colnames(fit2)[i], paste("d_",colnames(fit2)[i], sep=""))
}

write.table(as.list(header), file="__EXPERIMENT_NAME___results.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(df, file="__EXPERIMENT_NAME___results.txt", sep="\t", quote=F, col.names=F, append=T)

# also include topTables for all contrasts
## number=100

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=100, adjust.method="__PVALMETHOD__", lfc=__MIN_LOGFOLDCHANGE_2_WANTED__)
	write.table(tb, file=paste("detailed_results/top100table_", colnames(fit2)[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}

# ...and the full tables

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=nrow(fit2), adjust.method="__PVALMETHOD__", lfc=0)
	write.table(tb, file=paste("detailed_results/full_table_", colnames(fit2)[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}

contrast.desc <- colnames(fit2)






