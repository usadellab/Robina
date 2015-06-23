### Name: ppsetApply
### Title: Apply a function over the ProbeSets in an AffyBatch
### Aliases: ppsetApply ppset.ttest
### Keywords: manip

### ** Examples

ppset.ttest <- function(ppset, covariate, pmcorrect.fun = pmcorrect.pmonly, ...) {
  probes <- do.call("pmcorrect.fun", list(ppset))
  my.ttest <- function(x) {
    y <- split(x, get(covariate))
    t.test(y[[1]], y[[2]])$p.value
  }
  r <- apply(probes, 1, my.ttest)
  return(r)
}

## create a dataset
data(affybatch.example)
abatch <- merge(affybatch.example, affybatch.example2)
intensity(abatch) <- 2^jitter(log2(intensity(abatch)),1,1)
chip.variate <- c("a", "b", "a", "a", "b", "a")
phenoData(abatch) <- new("AnnotatedDataFrame", data=data.frame(whatever=chip.variate))
## run a test over _all_ probes.
all.ttest <- ppsetApply(abatch, ppset.ttest, covariate="whatever")




