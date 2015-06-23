### Name: esApply
### Title: An apply-like function for ExpressionSet and related structures.
### Aliases: esApply
### Keywords: models methods

### ** Examples

data(sample.ExpressionSet)
## sum columns of exprs
res <- esApply(sample.ExpressionSet, 1, sum)

## t-test, spliting samples by 'sex'
f <- function(x) {
    xx <- split(x, sex)
    t.test(xx[[1]], xx[[2]])$p.value
}
res <- esApply(sample.ExpressionSet, 1, f)

## same, but using a variable passed in the function call

f <- function(x, s) {
    xx <- split(x, s)
    mean(xx[[1]]) - mean(xx[[2]])
}
sex <- sample.ExpressionSet[["sex"]]
res <- esApply(sample.ExpressionSet, 1, f, s = sex)

# obtain the p-value of the t-test for sex difference
mytt.demo <- function(y) {
 ys <- split(y, sex)
 t.test(ys[[1]], ys[[2]])$p.value
}
sexPValue <- esApply(sample.ExpressionSet, 1, mytt.demo)

# obtain the p-value of the slope associated with score, adjusting for sex
# (if we were concerned with sign we could save the z statistic instead at coef[3,3]
myreg.demo <- function(y) {
   summary(lm(y ~ sex + score))$coef[3,4]
}
scorePValue <- esApply(sample.ExpressionSet, 1, myreg.demo)

# a resampling method
resamp <- function(ESET) {
 ntiss <- ncol(exprs(ESET))
 newind <- sample(1:ntiss, size = ntiss, replace = TRUE)
 ESET[newind,]
}

# a filter
q3g100filt <- function(eset) {
 apply(exprs(eset), 1, function(x) quantile(x,.75) > 100)
}

# filter after resampling and then apply
set.seed(123)
rest <- esApply({bool <- q3g100filt(resamp(sample.ExpressionSet)); sample.ExpressionSet[bool,]},
                1, mytt.demo)



