########################################################
## rankprod analysis of generic single channel arrays
##
########################################################

library(RankProd)

CLASSES 	<- c(
__PARAM_CLASSES__)

ORIGIN		<- c(
__PARAM_ORIGIN__)



# rank prod does not accept duplicated rows - these are usually
# control probes. The code below will calculate the mean/median signal for all redundant probes
# on each array and thereby collapsing the redundant rows to one


# remove redundant rows from the data frame
data.norm.nodup <- data.norm

if (length(rownames(data.norm$E) > length(unique(rownames(data.norm$E))))) {
	print ("__WARNINGS__", quote=F)
    print ("TYPE: Input data redundancy", quote=F)
    print ("SEVERITY:1", quote=F)
    print ("The input data contains redundant row names probably referring to control", quote=F)
    print ("probes. Robin will collapse the redundant rows to one containing the median", quote=F)
    print ("expression value for each chip. A list of all redundant probes found in the", quote=F)
    print ("dataset will be written to the file \"redundant.probes.info.txt\" in the", quote=F)
    print ("detailed_results folder.", quote=F)
    print ("", quote=F)

	uniprobes <- unique(rownames(data.norm$E))
	header <- c("probe.identifier", "number.of.spots", colnames(data.norm.nodup$E))
	write.table(as.list(header),
		file="detailed_results/redundant.probes.info.txt",
		sep="\t",
		quote=F,
		row.names=F,
		col.names=F)

	for (i in 1:length(uniprobes)) {
		ind <- which(rownames(data.norm$E) == uniprobes[i])
		hits <- length(ind)
		if (hits > 1) {
			#print(paste("ID:", uniprobes[i], "is redundant (", hits, ")\n"), quote=F)
			# tag all occurrences with NA for later removal
			data.norm.nodup$E[ind, ] <- NA

			# get the original values
			raw.values <- data.norm$E[ind, ]

			# calculate the median for each chip
			median.values <- t(as.data.frame(apply(raw.values, 2, median)))
			#mean.values <- t(as.data.frame(apply(raw.values, 2, mean)))

			# ... and attach the value as a new row to the data
			rownames(median.values) <- uniprobes[i]
			data.norm.nodup$E <- rbind(data.norm.nodup$E, median.values)

			# assemble row for output to file
			out.line <- c(length(ind), median.values)
			write.table(as.list(out.line),
				file="detailed_results/redundant.probes.info.txt",
				sep="\t",
				quote=F,
				row.names=uniprobes[i],
				append=T,
				col.names=F)
		}
	}
	print ("__WARNINGS_END__", quote=F)
	## get rid of redundant entries - can we do that?
	## however, if we don't, RPadvance breaks.
	data.norm.nodup$E <- na.exclude(data.norm.nodup$E)
}

RP.out <- RPadvance(data.norm.nodup$E, CLASSES, ORIGIN,
			num.perm=100,
			na.rm=F,
			logged=T,
			gene.names=rownames(data.norm.nodup$E),
			plot=F )

png(file="plots/RankProd_plot.png")
plotRP(RP.out, cutoff= 0.05)
dev.off()

top100 <- topGene(RP.out,
		num.gene=100,
		method="pfp",
		logged=T,
		logbase=2,
		gene.names=rownames(data.norm.nodup$E) )




header1 <- c("Identifier", colnames(top100$Table1))
write.table(as.list(header1), file="detailed_results/__EXPERIMENT_NAME___top100_upregulated.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(top100$Table1, file="detailed_results/__EXPERIMENT_NAME___top100_upregulated.txt", sep="\t", quote=F, append=T, col.names=F)

header2 <- c("Identifier", colnames(top100$Table2))
write.table(as.list(header2), file="detailed_results/__EXPERIMENT_NAME___top100_downregulated.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(top100$Table2, file="detailed_results/__EXPERIMENT_NAME___top100_downregulated.txt", sep="\t", quote=F, append=T, col.names=F)

write.table(RP.out, file="__EXPERIMENT_NAME___results_full.txt", sep="\t", quote=F)

headerMapMan <- c("Identifier", "aver_fold_change", "d_pval_up", "d_pval_down", "d_RPrank_up", "d_RPrank_down")
write.table(as.list(headerMapMan), file="detailed_results/__EXPERIMENT_NAME___results_short.txt", sep="\t", quote=F, row.names=F, col.names=F)
shortOut <- cbind(RP.out$AveFC, RP.out$pval, RP.out$RPrank)

# DO PVALUE ADJUSTMENT HERE!

write.table(shortOut, file="detailed_results/__EXPERIMENT_NAME___results_short.txt", sep="\t", quote=F, append=T, col.names=F)

if (__WRITE_RAW_EXPRS__) {
    # write the raw normalized expression values to file
    samplesHeader <- as.list(c("Identifier", basename(colnames(data.norm$E))))

    write.table(samplesHeader, sep="\t", col.names=FALSE, row.names=FALSE, file="detailed_results/__EXPERIMENT_NAME___raw_normalized_expression_values.txt", quote=FALSE)
    write.table(data.norm$E,file="detailed_results/__EXPERIMENT_NAME___raw_normalized_expression_values.txt",sep="\t",quote=F, col.names=FALSE, append=TRUE)
}
