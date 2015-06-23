## WH 30-Dec-2003
## see also vignette!
if ("package:matchprobes" %in% search()) detach("package:matchprobes")
library(matchprobes)
library(affy)
options(error=recover)

f1 <- system.file("extdata", "118T1.cel.gz", package="matchprobes")
f2 <- system.file("extdata", "CL2001032020AA.cel.gz", package="matchprobes")
pd1 <- new("AnnotatedDataFrame")
pData(pd1) <- data.frame(id="pi")
varLabels(pd1) <- list("phenovar")
pd2 <- new("AnnotatedDataFrame")
pData(pd2) <- data.frame(id="bh")
varLabels(pd2) <- list("phenovar")
x1 <- read.affybatch(filenames=f1, compress=TRUE, phenoData=pd1)
x2 <- read.affybatch(filenames=f2, compress=TRUE, phenoData=pd2)

res <- combine(list(x1, x2), c("hugeneflprobe","hgu95av2probe"), newcdf="comb")

comb <- res$cdf
z    <- rma(res$dat)

cat("sampleNames:", sampleNames(res$dat), sep="\n")

par(mfrow=c(2,2))
plot(exprs(res$dat), main="after combine", pch=".", log="xy")
plot(exprs(z),       main="after RMA", pch=".")
## the number of probes in each "new" probe set
prs <- multiget(ls(comb), comb)
nrprobes <- sapply(prs, function(x) nrow(x))
barplot(table(nrprobes), xlab="no probes in probeset", ylab="frequency")
