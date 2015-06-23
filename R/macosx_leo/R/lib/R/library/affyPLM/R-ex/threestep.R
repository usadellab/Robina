### Name: threestep
### Title: Three Step expression measures
### Aliases: threestep
### Keywords: manip

### ** Examples

data(affybatch.example)

# should be equivalent to rma()
eset <- threestep(affybatch.example)

# Using Tukey Biweight summarization
eset <- threestep(affybatch.example,summary.method="tukey.biweight")

# Using Average Log2 summarization
eset <- threestep(affybatch.example,summary.method="average.log")

# Using IdealMismatch background and Tukey Biweight and no normalization.
eset <- threestep(affybatch.example,normalize=FALSE,background.method="IdealMM",summary.method="tukey.biweight")

# Using average.log summarization and no background or normalization.
eset <- threestep(affybatch.example,background=FALSE,normalize=FALSE,background.method="IdealMM",summary.method="tukey.biweight")

# Use threestep methodology with the rlm model fit
eset <- threestep(affybatch.example,summary.method="rlm")

# Use threestep methodology with the log of the average
eset <- threestep(affybatch.example,summary.method="log.average")

# Use threestep methodology with log 2nd largest method
eset <- threestep(affybatch.example,summary.method="log.2nd.largest")

eset <- threestep(affybatch.example,background.method="LESN2")



