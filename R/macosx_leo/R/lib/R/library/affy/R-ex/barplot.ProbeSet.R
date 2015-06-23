### Name: barplot.ProbeSet
### Title: show a ProbeSet as barplots
### Aliases: barplot.ProbeSet
### Keywords: hplot

### ** Examples

data(affybatch.example)
gn <- geneNames(affybatch.example)
pps <- probeset(affybatch.example, gn[1])[[1]]

barplot.ProbeSet(pps)



