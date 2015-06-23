## show the variation of background values for all
## slides in a two color data set

quartz()
par(mfrow=c(ncol(RG$R), 4))

MA <- normalizeWithinArrays(RG, method="none", bc.method="none")

for (i in 1:ncol(RG$R)) {
	
	## red channel BG
	imageplot(	log2(RG$Rb[,i]), 
				RG$printer, 
				low="white", 
				high="red", 
				main=rownames(targets)[i])
	
	## green channel BG		
	imageplot(	log2(RG$Gb[,i]), 
				RG$printer, 
				low="white", 
				high="green", 
				main=rownames(targets)[i]) 
				
	## un-normalized log-ratios or M-values
	imageplot(MA$M[,i], RG$printer, zlim=c(-3,3), main=rownames(targets)[i]) 
	
	## MA plot
	plotMA(MA, array=i, main=rownames(targets)[i])
	abline(h=0, col="blue")
}

## plot unnormalized intensity distribution
quartz()
plotDensities(MA)
