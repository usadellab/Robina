### Name: expresso
### Title: From raw probe intensities to expression values
### Aliases: expresso bgcorrect.methods bgcorrect
### Keywords: manip

### ** Examples

data(affybatch.example)

eset <- expresso(affybatch.example, bgcorrect.method="rma",
                 normalize.method="constant",pmcorrect.method="pmonly",
                 summary.method="avgdiff")

##to see options available for bg correction type:
bgcorrect.methods



