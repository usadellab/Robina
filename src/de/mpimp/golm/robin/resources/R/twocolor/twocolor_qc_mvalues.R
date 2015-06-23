
## create image of the M values (unnormalized) of red vs. green channel signal
if (!exists("MA")) {
    MA <- normalizeWithinArrays(RG, method="none", bc.method="none")
}
for (i in 1:ncol(RG$R)) {
        outfile <- paste(tempRoot, "_mvalues_",i , ".png", sep="")
        png(file=outfile)
	
	## un-normalized log-ratios or M-values
	imageplot(MA$M[,i], RG$printer, zlim=c(-3,3), main=rownames(targets)[i]) 

        dev.off()
}
