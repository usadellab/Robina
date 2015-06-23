### Name: geneSetTest
### Title: Gene Set Test
### Aliases: geneSetTest
### Keywords: htest

### ** Examples

stat <- -9:9
sel <- c(2,4,5)
geneSetTest(sel,stat,alternative="down")
geneSetTest(sel,stat,alternative="either")
geneSetTest(sel,stat,alternative="down",ranks=FALSE)
sel <- c(1,19)
geneSetTest(sel,stat,alternative="mixed")
geneSetTest(sel,stat,alternative="mixed",ranks=FALSE)



