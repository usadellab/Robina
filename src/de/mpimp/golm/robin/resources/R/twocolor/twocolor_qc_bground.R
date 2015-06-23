
## create images of the red and green channel background signals
for (i in 1:ncol(RG$R)) {
        outfile <- paste(tempRoot, "_bground_",i , ".png", sep="")
        png(file=outfile, height=480, width=960)
        par(mfrow=c(1,2))
	
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
        dev.off()
}