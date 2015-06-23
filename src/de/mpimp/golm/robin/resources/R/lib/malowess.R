plotMAlowess <- function(M=NULL, A=NULL, smooth=2/3, iter=3, stats=F, array=NULL, sig=NULL, ...) {	
	warn <- FALSE
	badsamples <- character(0)
	
	par(cex=1)
	color <- "black"
	if (!is.null(sig)) {
		color <- ifelse(sig != 0, "red", "black")		
	}
	plot(A, M, pch=20, col=color, ...)	
	
	# omit NA values for loewss fit
	nona <- which( (!is.na(M) & (!is.na(A)) & (!is.infinite(M)) & (!is.infinite(A)) ) )
	M.nona <- M[nona]
	A.nona <- A[nona]
	
	# with lowess fit curve
	lowess.fit <- lowess(A.nona, M.nona, f=0.1, iter=3)
	lines(lowess.fit, col="green", lwd=2)

	# calculate the integral of the lowess fit over
	# the zero line (absolute values) to assess 
	# chip quality
	lx <- lowess.fit$x[-1]
	integral <- sum(abs(lowess.fit$y[-1])*(lx-lowess.fit$x[1:(length(lowess.fit$x)-1)]))
	
	abline(h=0, col="blue")
	abline(h=1, col="gray50", lty="dotted")
	abline(h=-1, col="gray50", , lty="dotted")
	
	if (stats) {
	
		# check percentage of genes above lfc 1
		lfc <- M[M>=1]
		perc <- round((length(lfc) / length(M)) * 100, 3)
	
		textcolLFC <- "black"
        textcolIntegral <- "black"
		# check the values and issue warnings
		if (integral > 1.5)  { # 1.5 is a more relaxed value; was 1 
			warn <- TRUE
			badsamples <- c(badsamples, array)
			textcolIntegral <- "red"
		} 

        if (perc > 5) {
            warn <- TRUE
            if ( !(array %in% badsamples)) {
                badsamples <- c(badsamples, array)
            }
            textcolLFC <- "red"
        }
	
		linespc = (max(M)-min(M)) / 25
		textX = min(A)+0.25
		textY = max(M)-linespc
	
		par(cex=0.75)
		text(textX, textY, paste("I =",as.character(round(integral,3))), adj=0, col=textcolIntegral)
		text(textX, textY-linespc, paste("%>LFC1 =",as.character(perc)),  adj=0, col=textcolLFC )
		text(textX, textY-linespc*2, paste("Median =",as.character(round(median(M), 3))), adj=0 )
	}
	
    if (warn) {
    	return(badsamples)
	}
        
}