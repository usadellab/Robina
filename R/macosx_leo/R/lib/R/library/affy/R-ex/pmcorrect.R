### Name: pmcorrect
### Title: PM Correction
### Aliases: pmcorrect pmcorrect.methods pmcorrect.pmonly pmcorrect.mas
###   pmcorrect.subtractmm
### Keywords: manip

### ** Examples

data(affybatch.example)
gn <- geneNames(affybatch.example)
pps <- probeset(affybatch.example, gn[1])[[1]]

pps.pmonly <- pmcorrect.pmonly(pps)
pps.subtractmm <- pmcorrect.subtractmm(pps)
pps.mas5 <- pmcorrect.mas(pps)
par(mfrow=c(2,2))
#plot(pm(pps.pmonly), pm(pps.subtractmm))
#plot(pm(pps.pmonly),pm(pps.mas5))
#plot(pm(pps.subtractmm),pm(pps.mas5))



