RGbox <- function(object=NULL, log=TRUE, ...) {
	
	if (is(object, "MAList")) {
        R <- object$A + object$M/2
        G <- object$A - object$M/2
        
        n <- basename(colnames(object$A))
        
        if (!log) {
            R <- 2^R
            G <- 2^G
        }
    } else {
        R <- object$R
        G <- object$G
        n <- basename(colnames(object$R))
    }
     
    #construct a list of RG value pair for each input chip
    #like Label.R, Label.G
    R.list <- split(R, col(R))
    G.list <- split(G, col(G))
    
    green.names <- lapply(n, paste, ".green", sep="")
    red.names <- lapply(n, paste, ".red", sep="")
    
    names(R.list) <- red.names
    names(G.list) <- green.names
    
    longestName <- nchar(max(n))
    
    # put together the list for plotting
    pl <- character(0)
    for (i in 1:ncol(R)) {
    	pl <- c(pl, R.list[i], G.list[i])
    } 
    
    par(cex=0.75, mar=c((longestName+4)*0.6, 4, 4, 2))
    boxplot(	pl,
    			las=2, 
    		 	col=rep(c("red", "green"), 3), ...)
}