### Name: normexp.fit
### Title: Fit Normal+Exp Convolution Model to Observed Intensities
### Aliases: normexp.fit
### Keywords: models

### ** Examples

f <- c(2,3,1,10,3,20,5,6)
b <- c(2,2,2,2,2,2,2,2)
out <- normexp.fit(f-b)
normexp.signal(out$par, x=f-b)



