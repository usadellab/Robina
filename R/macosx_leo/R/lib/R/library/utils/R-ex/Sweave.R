### Encoding: latin1

### Name: Sweave
### Title: Automatic Generation of Reports
### Aliases: Sweave Stangle SweaveSyntaxLatex SweaveSyntaxNoweb
### Keywords: utilities

### ** Examples

testfile <- system.file("Sweave", "Sweave-test-1.Rnw", package = "utils")

## enforce par(ask=FALSE)
options(device.ask.default=FALSE)

## create a LaTeX file
Sweave(testfile)

## create an S source file from the code chunks
Stangle(testfile)
## which can be simply sourced
source("Sweave-test-1.R")

## Don't show: 
if(!interactive()) unlink("Sweave-test-1*")
## End Don't show



