### Name: plotLocation
### Title: Plot a location on a cel image
### Aliases: plotLocation
### Keywords: aplot

### ** Examples

  ## loading data
  data(affybatch.example)

  ## image of the celfile
  image(affybatch.example[, 1])

  ## genenames, arbitrarily pick the 101th
  n <- geneNames(affybatch.example)[101]

  ## get the location for the gene n
  l <- indexProbes(affybatch.example, "both", n)[[1]]
  ## convert the index to X/Y coordinates
  xy <- indices2xy(l, abatch=affybatch.example) 

  ## plot
  plotLocation(xy)




