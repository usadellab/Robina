
## create MA plots of the unnormalized data
if (!exists("MA.un")) {
    MA.un <- normalizeWithinArrays(RG, method="none", bc.method="none")
}

if (!exists("MA.norm")) {
    normWithin <- __NORM_WITHIN__
    normBetween <- __NORM_BETWEEN__
    

    ## normalize the data
    if (normWithin) {
        MA.norm <- normalizeWithinArrays(RG, method="__NORM_WITHIN_METHOD__", bc.method="__BGCORR_METHOD__")
    }
    if (normBetween) {
        MA.norm <- normalizeBetweenArrays(MA.norm, method="__NORM_BETWEEN_METHOD__")
    }
}

bad <- character(0)

for (i in 1:ncol(RG$R)) {
        outfile <- paste(tempRoot, "_maplot_",i , ".png", sep="")
        png(file=outfile, width=1000, height=500)
        par(mfrow=c(1,2))
	
	## MA plots of normalized and unnormalzed data
        plotMAlowess(MA.un$M[,i], 
                     MA.un$A[,i],
                     smooth=0.2,
                     array=basename(rownames(targets)[i]),
                     main=basename(rownames(targets)[i]))

        bad <- c(bad, plotMAlowess(MA.norm$M[,i], 
                                   MA.norm$A[,i],
                                   smooth=0.2,
                                   stats=T,
                                   array=basename(rownames(targets)[i]),
                                   main=paste(basename(rownames(targets)[i]), "normalized")))
        dev.off()
}

if (length(bad) > 0) {
	print ("__WARNINGS__", quote=F)
    print ("TYPE:MAplot signal intensity", quote=F)
    print ("SEVERITY:1", quote=F)
    print ("The samples listed below show strong deviations", quote=F)
    print ("from expected values. Exclusion of these samples", quote=F)
    print ("from the analysis should be considered.", quote=F)
    print ("", quote=F)
    print (strwrap(bad), quote=F)
    print ("__WARNINGS_END__", quote=F)
}
