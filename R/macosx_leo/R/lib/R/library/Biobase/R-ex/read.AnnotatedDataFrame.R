### Name: read.AnnotatedDataFrame
### Title: Read 'AnnotatedDataFrame'
### Aliases: read.AnnotatedDataFrame
### Keywords: file manip

### ** Examples


exampleFile = system.file("extdata", "pData.txt", package="Biobase")

adf <- read.AnnotatedDataFrame(exampleFile)
adf
head(pData(adf))
head(readLines(exampleFile))




