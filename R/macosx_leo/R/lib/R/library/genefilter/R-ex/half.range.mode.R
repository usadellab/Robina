### Name: half.range.mode
### Title: Mode estimation for continuous data
### Aliases: half.range.mode
### Keywords: univar robust

### ** Examples

## A single normal-mixture data set

x <- c( rnorm(10000), rnorm(2000, mean = 3) )
M <- half.range.mode( x )
M.bs <- half.range.mode( x, B = 100 )

if(interactive()){
hist( x, breaks = 40 )
abline( v = c( M, M.bs ), col = "red", lty = 1:2 )
legend(
       1.5, par("usr")[4],
       c( "Half-range mode", "With bootstrapping (B = 100)" ),
       lwd = 1, lty = 1:2, cex = .8, col = "red"
       )
}

# Sampling distribution, with and without bootstrapping

X <- rbind(
           matrix( rnorm(1000 * 100), ncol = 100 ),
           matrix( rnorm(200 * 100, mean = 3), ncol = 100 )
           )
M.list <- list(
               Simple = apply( X, 2, half.range.mode ),
               BS = apply( X, 2, half.range.mode, B = 100 )
               )

if(interactive()){
boxplot( M.list, main = "Effect of bootstrapping" )
abline( h = 0, col = "red" )
}



