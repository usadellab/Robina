plotPrintTipLoessRobin <- function (object, layout, array = 1, span = 0.4, sample=NULL, ...) {
    if (is(object, "RGList")) {
        object <- MA.RG(object[, array])
        array <- 1
    }
    if (!is.null(object$printer) && missing(layout)) 
        layout <- object$printer
    y <- object$M[, array]
    x <- object$A[, array]
    if (dev.cur() == 1) 
        plot.new()
    df <- data.frame(y = object$M[, array], x = object$A[, array], 
        gr = factor(gridc(layout)), gc = factor(layout$ngrid.r - 
            gridr(layout) + 1))    
    coplot(y ~ x | gr * gc, data = na.omit(df), xlab = c("A", 
        paste("Tip Column", sample, sep=" - ")), ylab = c("M", "Tip Row"), pch = ".", span = span, 
        show.given = FALSE, panel = panel.smooth)
}


for (i in 1:ncol(RG$R)) {
	quartz()
	par(mfrow=c(2,2))
	plotPrintTipLoessRobin(	normalizeWithinArrays(RG, method="none", bc.method="none"), 
							sample=basename(rownames(targets)[i]),
							array=i)
	
	plotPrintTipLoessRobin(	MA.norm, 
							sample=basename(rownames(targets)[i]),
							array=i)
}
