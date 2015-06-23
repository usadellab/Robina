### Name: gcrma
### Title: Robust Multi-Array expression measure using sequence information
### Aliases: gcrma gcrma.bg.transformation.fast gcrma.bg.transformation
### Keywords: manip

### ** Examples

if(require(affydata) & require(hgu95av2probe) & require(hgu95av2cdf)){
     data(Dilution)
     ai <- compute.affinities(cdfName(Dilution))
     Dil.expr<-gcrma(Dilution,affinity.info=ai,type="affinities")
}



