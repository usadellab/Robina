### Name: xy2indices
### Title: Functions to convert indices to x/y (and reverse)
### Aliases: xy2indices indices2xy
### Keywords: manip

### ** Examples

data(affybatch.example)

pm.i <- indexProbes(affybatch.example, which="pm", genenames="AFFX-BioC-5_at")[[1]]
mm.i <- indexProbes(affybatch.example, which="mm", genenames="AFFX-BioC-5_at")[[1]]

pm.i.xy <- indices2xy(pm.i, abatch = affybatch.example)
mm.i.xy <- indices2xy(mm.i, abatch = affybatch.example)

image(affybatch.example[1], transfo=log2)
## plot the pm in red
plotLocation(pm.i.xy, col="red")
plotLocation(mm.i.xy, col="blue")




