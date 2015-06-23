###################################################
### chunk number 1: 
###################################################
library(affy)


###################################################
### chunk number 2: 
###################################################
normalize.AffyBatch.methods
bgcorrect.methods
pmcorrect.methods
express.summary.stat.methods


###################################################
### chunk number 3: 
###################################################
data(affybatch.example)
normalize.methods(affybatch.example)


###################################################
### chunk number 4: 
###################################################
pmcorrect.subtractmmsometimes <- function(object) {

  ## subtract mm
  mm.subtracted <- pm(object) - mm(object)

  ## find which ones are unwanted and fix them
  invalid <- which(mm.subtracted <= 0)
  mm.subtracted[invalid] <- pm(object)[invalid]

  return(mm.subtracted)
}


###################################################
### chunk number 5: 
###################################################
pmcorrect.methods <- c(pmcorrect.methods, "subtractmmsometimes")


###################################################
### chunk number 6: 
###################################################
huber <- function (y, k = 1.5, tol = 1e-06) {
    y <- y[!is.na(y)]
    n <- length(y)
    mu <- median(y)
    s <- mad(y)
    if (s == 0) 
        stop("cannot estimate scale: MAD is zero for this sample")
    repeat {
        yy <- pmin(pmax(mu - k * s, y), mu + k * s)
        mu1 <- sum(yy)/n
        if (abs(mu - mu1) < tol * s) 
            break
        mu <- mu1
    }
    list(mu = mu, s = s)
}


###################################################
### chunk number 7: 
###################################################
computeExprVal.huber <- function(probes) {
  res <- apply(probes, 2, huber)
  mu <- unlist(lapply(res, function(x) x$mu))
  s <- unlist(lapply(res, function(x) x$s))
  return(list(exprs=mu, se.exprs=s))
}

generateExprSet.methods <- c(generateExprSet.methods, "huber")


