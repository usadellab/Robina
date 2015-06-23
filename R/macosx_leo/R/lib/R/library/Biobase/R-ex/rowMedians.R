### Name: rowMedians
### Title: Calculates the median for each row in a matrix
### Aliases: rowMedians rowMedians,matrix-method
###   rowMedians,ExpressionSet-method
### Keywords: manip

### ** Examples

set.seed(1)
x <- rnorm(n=234*543)
x[sample(1:length(x), size=0.1*length(x))] <- NA
dim(x) <- c(234,543)
y1 <- rowMedians(x, na.rm=TRUE)
y2 <- apply(x, MARGIN=1, FUN=median, na.rm=TRUE)
stopifnot(all.equal(y1, y2))

x <- cbind(x1=3, x2=c(4:1, 2:5))
stopifnot(all.equal(rowMeans(x), rowMedians(x)))



