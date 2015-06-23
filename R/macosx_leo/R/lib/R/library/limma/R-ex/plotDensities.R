### Name: plotDensities
### Title: Individual-channel Densities Plot
### Aliases: plotDensities
### Keywords: hplot

### ** Examples

library(sma)
data(MouseArray)

#  no normalization but background correction is done
MA.n <- MA.RG(mouse.data)

#  Default settings for plotDensities.
plotDensities(MA.n)

#  One can reproduce the default settings.
plotDensities(MA.n,arrays=c(1:6),groups=c(rep(1,6),rep(2,6)),
col=c("red","green"))

#  Color R and G individual-channels by blue and purple.
plotDensities(MA.n,arrays=NULL,groups=NULL,col=c("blue","purple"))

#  Indexing individual-channels using singlechannels (arrays=NULL).
plotDensities(MA.n,singlechannels=c(1,2,7))

#  Change the default colors from c("red","green") to c("pink","purple")
plotDensities(MA.n,singlechannels=c(1,2,7),col=c("pink","purple"))

#  Specified too many colors since groups=NULL defaults to two groups.
plotDensities(MA.n,singlechannels=c(1,2,7),col=c("pink","purple","blue"))

#  Three individual-channels, three groups, three colors.
plotDensities(MA.n,singlechannels=c(1,2,7),groups=c(1,2,3),
col=c("pink","purple","blue"))

#  Three individual-channels, one group, one color.
plotDensities(MA.n,singlechannels=c(1,2,7),groups=c(1,1,1),
col=c("purple"))

#  All individual-channels, three groups (ctl,tmt,reference), three colors.
plotDensities(MA.n,singlechannels=c(1:12),
groups=c(rep(1,3),rep(2,3),rep(3,6)),col=c("darkred","red","green"))




