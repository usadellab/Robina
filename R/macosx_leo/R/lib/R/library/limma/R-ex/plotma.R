### Name: plotMA
### Title: MA-Plot
### Aliases: plotMA
### Keywords: hplot

### ** Examples

MA <- new("MAList")
MA$A <- runif(300,4,16)
MA$M <- rt(300,df=3)
status <- rep("Gene",300)
status[1:3] <- "M=0"
MA$M[1:3] <- 0
status[4:6] <- "M=3"
MA$M[4:6] <- 3
status[7:9] <- "M=-3"
MA$M[7:9] <- -3
plotMA(MA,main="MA-Plot with Simulated Data",status=status,values=c("M=0","M=3","M=-3"),col=c("blue","red","green"))

#  Same as above
attr(status,"values") <- c("M=0","M=3","M=-3")
attr(status,"col") <- c("blue","red","green")
plotMA(MA,main="MA-Plot with Simulated Data",status=status)

#  Same as above
MA$genes$Status <- status
plotMA(MA,main="MA-Plot with Simulated Data")



