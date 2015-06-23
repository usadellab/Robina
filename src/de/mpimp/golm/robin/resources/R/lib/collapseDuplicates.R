#################################
# eliminate duplicate ID lines from
# Robin results data frame by collapsing
# the values to their contrast-wise
# median. Also removes NA values
# This step should make any kind of
# result table loadable in MapMan
#################################

# remove redundant rows from the data frame
df.nodup <- df

if (length(rownames(df) > length(unique(rownames(df))))) {
	print ("__WARNINGS__", quote=F)
    print ("TYPE: Data redundancy", quote=F)
    print ("SEVERITY:1", quote=F)
    print ("The input data contains redundant row names probably referring to control", quote=F)
    print ("probes. Robin will collapse the redundant rows to one containing the median", quote=F)
    print ("log2-fold change value for each contrast and write the filtered results to.", quote=F)
    print ("a separate tabular text file with the extension '.noduplicates'. This file ", quote=F)
    print ("can be imported into the MapMan application without further modification.", quote=F)
    print ("", quote=F)

	uniprobes <- unique(rownames(df))

	for (i in 1:length(uniprobes)) {
		ind <- which(rownames(df) == uniprobes[i])
		hits <- length(ind)
		if (hits > 1) {
			#print(paste("ID:", uniprobes[i], "is redundant (", hits, ")\n"), quote=F)
			# tag all occurrences with NA for later removal
			df.nodup[ind, ] <- NA

			# get the original values and force them to be numeric
			raw.values <- t(apply(df[ind, ], 1, as.numeric))

			# calculate the median for each contrast
			#lfc.colind <- seq(from=1, to=ncol(df.nodup), by=2)
			#median.values.lfc <- t(as.data.frame(apply(raw.values[, lfc.colind], 2, median, na.rm=TRUE)))
			median.values <- t(as.data.frame(apply(raw.values, 2, median, na.rm=TRUE)))
			#mean.values <- t(as.data.frame(apply(raw.values, 2, mean)))

			# ... and attach the value as a new row to the data
			rownames(median.values) <- uniprobes[i]
			df.nodup <- rbind(df.nodup, median.values)
		}
	}
	print ("__WARNINGS_END__", quote=F)
	## get rid of redundant entries - can we do that?
	## however, if we don't, RPadvance breaks.
	df.nodup <- na.exclude(df.nodup)
}
header.nodup <- c("Identifier")

for (i in 1:ncol(fit2)) {
	if (length(colnames(fit2)[i]) == 0) {
		header.nodup <- c(header.nodup, contrast.desc[i], paste("d_",contrast.desc[i], sep=""))
	} else {
		header.nodup <- c(header.nodup, contrast.desc[i], paste("d_",contrast.desc[i], sep=""))
	}
}

write.table(as.list(header.nodup), file="__EXPERIMENT_NAME___results.noduplicates.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(df.nodup, file="__EXPERIMENT_NAME___results.noduplicates.txt", sep="\t", quote=F, col.names=F, append=T)
