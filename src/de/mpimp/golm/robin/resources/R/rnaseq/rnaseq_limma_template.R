library(edgeR)
library(limma)

source("source/lib/ellipse.R")
source("source/lib/robinVennDiagram.R")
source("source/lib/malowess.R")

###################################
# settings to be completed by GUI #
###################################

P_VAL_ADJUST_METHOD <- "__PVALMETHOD__"
P_VAL_CUTOFF        <- __PARAM_PVALCUTOFF__
MULTI_TEST_STRATEGY <- "__MULTITEST_STRAT__"
MIN_LFC2_ONE        <- __MIN_LOGFOLDCHANGE_2_WANTED__


design <- model.matrix(~ -1+groups)
colnames(design) <- as.character(unique(groups))

d <- DGEList(counts=raw, group=groups)
d <- calcNormFactors(d)

elist <- voom(d, design=design)


contrast.matrix <- makeContrasts(
    __CONTRAST_TERMS__
    ,levels=design
)

fit <- lmFit(elist, design)
fit2 <- contrasts.fit(fit, contrast.matrix)
fit2 <- eBayes(fit2)


# test all hypotheses in the contrast matrix
results <- decideTests(fit2, method=MULTI_TEST_STRATEGY,
                             adjust.method=P_VAL_ADJUST_METHOD,
                             lfc=MIN_LFC2_ONE,
                             p.value=P_VAL_CUTOFF)

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




if (TRUE) {
    # write the raw normalized expression values to file
    samplesHeader <- as.list(c("Identifier", basename(colnames(elist$E))))

    write.table(samplesHeader, sep="\t", col.names=FALSE, row.names=FALSE, file="detailed_results/raw_normalized_expression_values.txt", quote=FALSE)
    write.table(elist$E, file="detailed_results/raw_normalized_expression_values.txt",sep="\t",quote=F, col.names=FALSE, append=TRUE)
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

    plotMAlowess(M=fit2$coef[, i], A=fit2$A, main=paste(sep=" ",
                        "MA Plot of contrast",
                        colnames(fit2$coef)[i]), sig=signif)   
    dev.off()
}

## group PCA plot with ellipse around clustering points
## if the correlation among them is high enough

pca <- prcomp(t(na.omit(elist$E)))

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


for (i in 1:length(levels(groups))) {

	points(pca$x[groups == levels(groups)[i], 1], pca$x[groups == levels(groups)[i], 2], col=i, pch=i )

	center.x <- mean(pca$x[groups ==levels(groups)[i], 1])
	center.y <- mean(pca$x[groups ==levels(groups)[i], 2])

	a <- (max(pca$x[groups ==levels(groups)[i], 1]) - min(pca$x[groups ==levels(groups)[i], 1]))
	b <- (max(pca$x[groups ==levels(groups)[i], 2]) - min(pca$x[groups ==levels(groups)[i], 2]))

	ellipse(center=c(center.x, center.y), radius=c(a,b), rotate=0, add=T, col=i, lty="dotted")
}
par(mai = c(0,0,1.01,0))
plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
legend("topleft", legend=levels(groups), col=c(1:length(levels(groups))), pch=c(1: length(levels(groups))), bty="n", cex=0.75)
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


for (i in 1:length(levels(groups))) {

	points(pca$x[groups == levels(groups)[i], 1], pca$x[groups == levels(groups)[i], 2], col=i, pch=i )

	center.x <- mean(pca$x[groups ==levels(groups)[i], 1])
	center.y <- mean(pca$x[groups ==levels(groups)[i], 2])

	a <- (max(pca$x[groups ==levels(groups)[i], 1]) - min(pca$x[groups ==levels(groups)[i], 1]))
	b <- (max(pca$x[groups ==levels(groups)[i], 2]) - min(pca$x[groups ==levels(groups)[i], 2]))

	ellipse(center=c(center.x, center.y), radius=c(a,b), rotate=0, add=T, col=i, lty="dotted")
}
par(mai = c(0,0,1.01,0))
plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
legend("topleft", legend=levels(groups), col=c(1:length(levels(groups))), pch=c(1: length(levels(groups))), bty="n", cex=0.75)
dev.off()


png(file=paste(sep="","plots/hclust", ".png"),
        width=600,
        height=600)        
plot( hclust(as.dist(1-cor(na.omit(elist$E)))) )
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

result.file.name <- paste(PROJECT_NAME, "_results.txt", sep="")

write.table(as.list(header), file=result.file.name, sep="\t", quote=F, row.names=F, col.names=F)
write.table(df, file=result.file.name, sep="\t", quote=F, col.names=F, append=T)

# also include topTables for all contrasts
## number=100

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=100, adjust.method=P_VAL_ADJUST_METHOD, lfc=MIN_LFC2_ONE)
	write.table(tb, file=paste("detailed_results/top100table_", colnames(fit2)[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}

# ...and the full tables

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=nrow(fit2), adjust.method=P_VAL_ADJUST_METHOD, lfc=MIN_LFC2_ONE)
	write.table(tb, file=paste("detailed_results/full_table_", colnames(fit2)[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}


write.short.sessionInfo("source/R.session.info.txt")
