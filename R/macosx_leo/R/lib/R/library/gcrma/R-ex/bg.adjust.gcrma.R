### Name: bg.adjust.gcrma
### Title: GCRMA background adjust (internal function)
### Aliases: bg.adjust.gcrma
### Keywords: manip

### ** Examples

 if(require(affydata) & require(hgu95av2probe) & require(hgu95av2cdf)){
          data(Dilution)
          ai <- compute.affinities(cdfName(Dilution))
          Dil.adj<-bg.adjust.gcrma(Dilution,affinity.info=ai,type="affinities")
     }



