### Name: dupcor
### Title: Correlation Between Duplicates
### Aliases: duplicateCorrelation
### Keywords: multivariate

### ** Examples

#  Also see lmFit examples

## Not run: 
##D corfit <- duplicateCorrelation(MA, ndups=2, design)
##D all.correlations <- tanh(corfit$atanh.correlations)
##D boxplot(all.correlations)
##D fit <- lmFit(MA, design, ndups=2, correlation=corfit$consensus)
## End(Not run)



