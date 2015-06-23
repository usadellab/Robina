
###########################################################
##
##  Copyright 2005 James W. MacDonald
##
## affystart - functions to go from celfiles to exprSet
##             with QC plots
##
########################################################

##################
#
# The codes was modified by M. Lohse in several places to change the
# graphical appearance of the output and include additional info.
# Modifications are commented in the code.
#
#################

plotHist <- function(dat, filenames = NULL, farbe=NULL)
{
    layout(matrix(1:2, nc = 2), c(3,1))		
    if(is.null(filenames)) filenames <- sampleNames(dat)

    if (is.null(farbe)) 
  	cl <- make.cl(filenames)
    else   
  	cl <- farbe
  if(length(filenames) <= 8){
    if(is(dat, "AffyBatch"))
      hist(dat, lty=1, lwd=2, col=cl)
    if(is(dat, "matrix"))
      plotDensity(log2(dat), lty = 1, lwd = 2, col = cl)
    x.ax <- legend(1,1,legend=filenames, lty=1, lwd=2, col=cl, plot=FALSE)$rect$w
    
    par(mai = c(0,0,1.01,0))
  	plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
  	par(cex=0.8)
    legend("topleft",legend=filenames, lty=1, lwd=2, col=cl)
  }else{
    if(is(dat, "AffyBatch"))
      hist(dat, lty=1, lwd=2, col=cl)
    if(is(dat, "matrix"))
      plotDensity(log2(dat), lty = cl, lwd = 2, col = 1:length(filenames))
    x.ax <- legend(1,1,legend=filenames, lty=1, lwd=2, col=cl, plot=FALSE)$rect$w
    y.ax <- legend(1,1,legend=filenames, lty=1, lwd=2, col=cl, plot=FALSE)$rect$h
    ydiff <- par("usr")[4] - par("usr")[3]
    ## If legend is too big, shrink to fit
    if(y.ax < ydiff){
    	
    	par(mai = c(0,0,1.01,0))
  	  plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
  	  par(cex=0.8)
      legend("topleft", legend=filenames, lty=1, lwd=2, col=cl)
    }else{
      cexval <- 1
      while(y.ax > ydiff){
        cexval <- cexval - 0.05
        y.ax <- legend(1,1, legend=filenames, lty=1, lwd=2, col=cl, plot=FALSE, cex=cexval)$rect$h
        x.ax <- legend(1,1,legend=filenames, lty=1, lwd=2, col=cl, plot=FALSE, cex=cexval)$rect$w
      }
      
      par(mai = c(0,0,1.01,0))
  	  plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
  	  par(cex=cexval)
      legend("topleft", legend=filenames, lty=1, lwd=2, col = 1:length(filenames), cex=cexval)
    }
    
  }
}


plotDeg <- function(dat, filenames = NULL, farbe=NULL){
  if(is.null(filenames)) filenames <- sampleNames(dat)
  if(is.null(farbe)) farbe <- 1:length(filenames)

  ## reset things when exiting
  op <- par(no.readonly = TRUE)
  on.exit(par(op))
  
  ## put plot on left, legend on right
  layout(matrix(1:2, nc = 2), c(3,1))
  
  rna <- AffyRNAdeg(dat)  

  ## modified the script to report bad quality chips
  ## cut-off is a slope (5'/3') of 3
  bad <- as.list(rna$sample.names[rna$slope > 3]) 

  ## the deviation in the group should
  ## also be taken into account here
  ## since it is also very important that
  ## all chips show more or less the same
  ## RNA quality
  if (length(bad) > 0) {
      print("__WARNINGS__", quote=F)
      print ("TYPE:RNA Degradation", quote=F)
      print ("SEVERITY:1", quote=F)
      print("The following chips' 5'/3' ratio", quote=F)
      print("indicates advanced degradation of", quote=F)
      print("the RNA sample used. Including them", quote=F)
      print("in the analysis is not recommended.", quote=F)
      print("", quote=F)
      print(paste(bad, sep=", "), quote=F)
      print("__WARNINGS_END__", quote=F)      
  }
  
  slope.median <- median(rna$slope)
  outliers <- as.list(rna$sample.names[rna$slope > (slope.median+(0.3*slope.median))])
  outliers <- as.list(outliers, rna$sample.names[rna$slope < (slope.median-(0.3*slope.median))])
  
  if (length(outliers) > 0) {
      print("__WARNINGS__", quote=F)
      print ("TYPE:RNA Degradation", quote=F)
      print ("SEVERITY:1", quote=F)
      print("The following chips' 5'/3' ratio", quote=F)
      print("deviates by more than 30% from the", quote=F)
      print("median 3'/5' ratio of the other chips", quote=F)
      print("in the experiment. Excluding them from", quote=F)
      print("the analysis should be considered.", quote=F)
      print("", quote=F)
      print(paste(outliers, sep=", "), quote=F)
      print("__WARNINGS_END__", quote=F)      
  }
 
  plotAffyRNAdeg(rna, col=farbe)

  ## fake a plot
  par(mai = c(0,0,1.01,0))
  plot(1:10, type = "n", xaxt = "n", yaxt = "n", xlab = "", ylab = "", bty = "n")
  
  tmp <- legend("topleft", legend=filenames, lty=1, lwd=2,
                col=farbe, plot=FALSE)

  y.ax <- tmp$rect$h
  x.ax <- tmp$rect$w
  ydiff <- par("usr")[4] - par("usr")[3]
  xdiff <- par("usr")[2] - par("usr")[1]

  
  ## If legend is too big, shrink to fit
  if(y.ax < ydiff && x.ax < xdiff){
    legend("topleft", legend=filenames, lty=1, lwd=2, col=farbe)
  }else{
    cexval <- 1
    while(y.ax > ydiff){
      cexval <- cexval - 0.05
      tmp <- legend("topleft", legend=filenames, lty=1, lwd=2,
                    col=farbe, plot=FALSE, cex=cexval)
      y.ax <- tmp$rect$h
      x.ax <- tmp$rect$w
    }
    if(x.ax < xdiff){
      legend("topleft", legend=filenames, lty=1, lwd=2, col=farbe, cex=cexval)
    }else{
      while(x.ax > xdiff){
        cexval <- cexval - 0.05
        x.ax <- legend("topleft", legend=filenames, lty=1, lwd=2,
                       col=farbe, plot=FALSE, cex=cexval)$rect$w
      }
      legend("topleft", legend=filenames, lty=1, lwd=2, col=farbe, cex=cexval)
    }
  }
}

plotPCA <- function(eset, groups = NULL, groupnames = NULL, addtext = NULL, x.coord = 10, y.coord = 10,
                    screeplot = FALSE, squarepca = FALSE, pch = NULL, col = NULL, ...){
  if(is.null(groupnames)) groupnames <- sampleNames(eset)
  if(is.factor(groupnames)) groupnames <- as.character(groupnames)
  pca <- prcomp(t(exprs(eset)))
  if(screeplot){
    plot(pca, main = "Screeplot")
  }else{
    if(squarepca){
      ylim <- max(abs(range(pca$x[,1])))
      ylim <- c(-ylim, ylim)
    }else ylim <- NULL
    if(!is.null(groups)){
      if(is.null(pch)) pch <- groups
      if(is.null(col)) col <- groups
      plot(pca$x[,1:2], pch = pch, col = col, ylab="PC2", xlab="PC1",
           main="Principal Components Plot", ylim = ylim, ...)
    }else{
      if(is.null(pch)) pch <- 0:length(sampleNames(eset))
      if(is.null(col)) col <- 1:length(sampleNames(eset))
      plot(pca$x[,1:2], pch=pch, col=col,
           ylab="PC2", xlab="PC1", main="Principal Components Plot", ylim = ylim, ...)
    }
    if(is.null(addtext)){
      pca.legend(pca, groupnames, pch, col, x.coord = x.coord, y.coord = y.coord, ...)
    }else{
      smidge <-  pca.legend(pca, groupnames, pch, col, x.coord = x.coord, y.coord = y.coord,
                            saveup = TRUE, ...)
      text(pca$x[,1], pca$x[,2] + smidge, label = addtext, cex = 0.7)
    }
  }
}

plotPCA.mod <- function(eset, groups = NULL, groupnames = NULL, addtext = NULL, x.coord = 10, y.coord = 10,
                    screeplot = FALSE, squarepca = FALSE, pch = NULL, col = NULL, pc=list(c(1,2)), ...) {

  if (is.null(groupnames))
  		groupnames <- sampleNames(eset)

  if (is.factor(groupnames))
  		groupnames <- as.character(groupnames)

  pca <- prcomp(t(exprs(eset)))

  if (screeplot){
  	# always keep two columns, adjust number of rows
  	par(mfrow=c(ceiling((length(pc)+1)/2), 2))
  	#
    plot(pca, main = "Screeplot")
  }
  	# PCA plots start here
    for (i in 1:length(pc)) {

        if ( ( pc[[i]][1] > ncol(pca$x) )||( pc[[i]][2] > ncol(pca$x) ) ) {
            break
  	}

      	if(is.null(pch)) pch <- 0:length(sampleNames(eset))
      	if(is.null(col)) col <- 1:length(sampleNames(eset))

      	plot(pca$x[, pc[[i]][1]:pc[[i]][2]], pch=pch, col=col,
           	ylab=paste("PC", pc[[i]][2], sep=""), xlab=paste("PC", pc[[i]][1], sep=""), main="Principal Components Plot", ...)

    	if (is.null(addtext)) {
      		pca.legend(pca, groupnames, pch, col, x.coord = x.coord, y.coord = y.coord, ...)
    	} else {
      		smidge <-  pca.legend(pca, groupnames, pch, col, x.coord = x.coord, y.coord = y.coord,
                            saveup = TRUE, ...)
      		text(pca$x[,1], pca$x[,2] + smidge, label = addtext, cex = 0.7)
    	}
    }
}


make.cl <- function(filenames){
  ## A function to make a classlabel for plotting
  ## Check for number of files
  if(length(filenames) <= 8)
    cl <- 1 : length(filenames)
  if(length(filenames) > 8){
    mod <- floor(length(filenames)/8)
    cl <- NULL
    for(i in 1 : mod){
      cl <- c(cl, rep(i, 8))
    }
    rem <- length(filenames) - mod*8
    cl <- c(cl, rep(mod + 1, rem))
  }
  cl
}



pca.legend <- function(pca, groupnames, pch, col, x.coord = NULL, y.coord = NULL,
                       saveup = FALSE){
  ## A function to try to automagically place legend in a pca plot
  par(cex=0.75)
  #pch <- sort(unique(pch))
  #col <- sort(unique(col))
  x.lab <- legend(1, 1, legend = groupnames, pch = pch, plot = FALSE)$rect$w
  y.lab <- legend(1, 1, legend = groupnames, pch = pch, plot = FALSE)$rect$h
  

  right <- par("usr")[2] - (par("usr")[2] - par("usr")[1])/100 - x.lab
      left <- par("usr")[1] + (par("usr")[2] - par("usr")[1])/100 + x.lab
  up <- par("usr")[4] - (par("usr")[4] - par("usr")[3])/100 - y.lab
  down <- par("usr")[3] + (par("usr")[4] - par("usr")[3])/100 + y.lab
  
  upright <- !any(pca$x[,1] > right & pca$x[,2] > up)
  upleft <- !any(pca$x[,1] < left & pca$x[,2] > up)
  downright <- !any(pca$x[,1] > right & pca$x[,2] < down)
  downleft <- !any(pca$x[,1] < left & pca$x[,2] < down)
  
  where <- match(TRUE, c(upright, upleft, downleft, downright))
  if(!is.na(where)){
    if(where == 1)
      legend(right, up + y.lab, legend=groupnames, pch=pch, col=col, bty="n")
    if(where == 2)
      legend(left - x.lab, up + y.lab, legend=groupnames, pch=pch, col=col, bty="n")
    if(where == 3)
      legend(left - x.lab, down, legend=groupnames, pch=pch, col=col, bty="n")
    if(where == 4)
      legend(right, down, legend=groupnames, pch=pch, col=col, bty="n")
  }else if(!is.null(x.coord) & !is.null(y.coord)){
    legend(x.coord, y.coord, legend = groupnames, pch = pch, col = col, bty="n")
  }else{
    answer <- readline("Please give the x-coordinate for a legend.")
    x.c <- as.numeric(answer)
    answer <- readline("Please give the y-coordinate for a legend.")
    y.c <- as.numeric(answer)
    legend(x.c, y.c, legend=groupnames, pch=pch, col=col, bty="n")
  }
  if(saveup)
    return((par("usr")[4] - par("usr")[3])/50)
}


