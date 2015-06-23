## template for limma-based analysis of 
## common reference design
## microarray experiments

source("source/lib/robinVennDiagram.R")

normWithin <- __NORM_WITHIN__
normBetween <- __NORM_BETWEEN__
writeRaw <- __WRITE_RAW_EXPRS__

## normalize the data
if (normWithin) {
    MA <- normalizeWithinArrays(RG, method="__NORM_WITHIN_METHOD__", bc.method="__BGCORR_METHOD__")
} else {
    MA <- normalizeWithinArrays(RG, method="none", bc.method="none")
}
if (normBetween) {
    MA <- normalizeBetweenArrays(MA, method="__NORM_BETWEEN_METHOD__")
}

#######################################
# Here we are trying to deal with the
# fact that replicate gene spots may 
# have dynamic spacing
#######################################

## are there duplicates at all?
has.dups 	<- FALSE
len.total	<- length(MA$genes$"__ID_COLUMN_START__")
len.unique      <- length(unique(MA$genes$"__ID_COLUMN_START__"))

## if the gene spots are not duplicated (but the controls are, which is most
## probably true) the number of unique identifiers will be greater than 50%
## of the total number of spots. This should be true for all array types that
## have more gene spots than control spots

if (len.unique > len.total * 0.5) {
	print("no duplicate spots detected.", quote=F)
	print("inserting unique ID according to chip layout.", quote=F)
	
	library(marray)
	L <- new("marrayLayout",
                    maNgr = printLayout$ngrid.r,
                    maNgc = printLayout$ngrid.c,
                    maNsr = printLayout$nspot.r,
                    maNsc = printLayout$nspot.c)

	arrayID <- apply(maInd2Coord(seq(maNspots(L)), L),1, paste, sep=".",collapse=".")
	
	#MA$genes$"__ID_COLUMN_START__" <- arrayID
} else {
	
	## sort gene IDs ascending
	a <- order(MA$genes$"__ID_COLUMN_START__")
	genes.sorted <- MA$genes$"__ID_COLUMN_START__"[a]

	## if the spots are in even replicates
	## these two vectors should be identical
	a1 <- genes.sorted[seq(1,length(a), by=2)]
	a2 <- genes.sorted[seq(2,length(a), by=2)]
	a3 <- a1==a2

	if (FALSE %in% a3) {
                print ("__WARNINGS__", quote=F)
		print ("TYPE:Undiscernable number of replicate spots detected", quote=F)
		print ("SEVERITY:3", quote=F)
		print ("The chip data contains replicate spots with an", quote=F)
		print ("uneven or heterogeneous number of replicates.", quote=F)
		print ("Robin could not automatically determine the replicate", quote=F)
		print ("structure. Stopping analysis.", quote=F)
                print ("__WARNINGS_END__", quote=F)
                stop("cannot analyse dataset - undiscernable number of replicates")
	} else {
		## if we reach this point the sorting is OK
                print ("__WARNINGS__", quote=F)
		print ("TYPE:Duplicate spots detected", quote=F)
		print ("SEVERITY:1", quote=F)
		print ("The chosen chip layout contains replicate spots.", quote=F)
		print ("Robin assumed the number of duplicates to be 2; If", quote=F)
		print ("the number of duplicates is higher the results will", quote=F)
                print ("be incorrect.", quote=F)		
		print ("__WARNINGS_END__", quote=F)
		print("duplicate spots detected. input reordered to give ndups=2, spacing=1", quote=F)
		MA <- MA[a,]
		
		# here we still have a problem if the ndups is even but greater than 2
		MA$printer$ndups 	<- 2
		MA$printer$spacing 	<- 1
		has.dups <- TRUE
	}
}
################################################


## If there are at least two arrays with each dye-orientation, then it is possible to estimate 
## and adjust for any probe-speciﬁc dye eﬀects. The dye-eﬀect is estimated by an intercept term.

design <- modelMatrix(targets, ref="__REFERENCE_SAMPLE__")
dup.corr <- NULL

################################################
if (has.dups) {
	dup.corr <- duplicateCorrelation(MA,
                                        design=design,
                                        ndups=MA$printer$ndups,
                                        spacing=MA$printer$spacing,
                                        block=NULL,
                                        trim=0.15,
                                        weights=NULL)
}



################################################

fit <- lmFit(MA, design, correlation=dup.corr$consensus.correlation)

contrast.matrix <- makeContrasts(__CONTRAST_TERMS__, levels=design)

contrast.desc <- c(__CONTRAST_NAMES__)

fit2 <- contrasts.fit(fit, contrast.matrix)

fit2 <- eBayes(fit2)

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
    print("targets table.", quote=F)
    print("__WARNINGS_END__", quote=F) 
}

header <- c("Identifier")

df <- character(0)

for (i in 1:ncol(fit2)) {
	if (ncol(fit2)==1) {
		df <- cbind(fit2$coef, results[,i])
	} else {
		df <- cbind(df, fit2$coef[,i], results[,i])
	}

	if (length(colnames(fit2)[i]) == 0) {
		header <- c(header, contrast.desc[i], paste("d_",contrast.desc[i], sep=""))
	} else {
		header <- c(header, contrast.desc[i], paste("d_",contrast.desc[i], sep=""))
	}
}

if (AGILENT_FIX) {
	rownames(df) <- rownames(MA$M)
} else {
	rownames(df) <- fit2$genes$"__ID_COLUMN_START__"
}

write.table(as.list(header), file="__EXPERIMENT_NAME___results.txt", sep="\t", quote=F, row.names=F, col.names=F)
write.table(df, file="__EXPERIMENT_NAME___results.txt", sep="\t", quote=F, col.names=F, append=T)

## MA plots for each contrast highlighting the significantly changed genes in red circles
if (ncol(fit2$coef)==0)  {
	png(file=paste(sep="","plots/MAplot.png"),
        	width=600,
        	height=600)
	signif.genes <- abs(results[,i])

    #par(cex=0.5)
    plot(   fit2$A,
            fit2$coef,
            main=paste(	sep=" ",
                        "MA Plot",
                        colnames(fit2$coef)[i]),
            ylab="M",
            xlab="A",
            pch=".")

    # significant points will be larger
    #par(cex=2)
    points( fit2$A[signif.genes==1],
            fit2$coef[signif.genes==1],
            col="red",
            pch="o")
    abline(h=0, col="blue")
    dev.off();
} else {

	for (i in 1:ncol(fit2$coef)) {
    	png(file=paste(sep="","plots/MAplot_", contrast.desc[i], ".png"),
        	width=600,
        	height=600)
    	signif.genes <- abs(results[,i])

    	#par(cex=0.5)
    	plot(   fit2$A,
        	    fit2$coef[, i],
            	main=paste(	sep=" ",
                	        "MA Plot of contrast",
                    	    contrast.desc[i]),
            	ylab="M",
            	xlab="A",
            	pch=".")

    	# significant points will be larger
    	#par(cex=1)
    	points( fit2$A[signif.genes==1],
            	fit2$coef[signif.genes==1, i],
            	col="red",
            	pch="o")
    	abline(h=0, col="blue")
    	dev.off()
	}
}


# and draw venn diagrams showing the number of
# genes reacting significantly in each comparison
# ONLY IF 4 AND LESS CONTRASTS (using the extended Venn code)
if (length(ncol(fit2$coef)) <= 4) {
    png(filename="plots/vennDiagram_total.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, names=contrast.desc, main="Significantly regulated genes", include="both")
    dev.off()

    png(filename="plots/vennDiagram_up.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, names=contrast.desc, main="Upregulated genes", include="up")
    dev.off()

    png(filename="plots/vennDiagram_down.png", height=6, width=10, units="in", res=150)
    par(cex = 0.75)
    robinVennDiagram(results, names=contrast.desc, main="Downregulated genes", include="down")
    dev.off()
}


# also include topTables for all contrasts
## number=100

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=100, adjust.method="BH", lfc=1)
	write.table(tb, file=paste("detailed_results/top100table_", contrast.desc[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}

# ...and the full tables

for (i in 1:ncol(fit2)) {

	tb <- topTable(fit2, coef=i, number=nrow(fit2), adjust.method="BH", lfc=0)
	write.table(tb, file=paste("detailed_results/full_table_", contrast.desc[i], ".txt", sep=""),
				sep="\t",
				quote=F,
				row.names=F)
}
