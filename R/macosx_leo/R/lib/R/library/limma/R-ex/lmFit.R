### Name: lmFit
### Title: Linear Model for Series of Arrays
### Aliases: lmFit
### Keywords: models regression

### ** Examples

# Simulate gene expression data for 100 probes and 6 microarrays
# Microarray are in two groups
# First two probes are differentially expressed in second group
# Std deviations vary between genes with prior df=4
sd <- 0.3*sqrt(4/rchisq(100,df=4))
y <- matrix(rnorm(100*6,sd=sd),100,6)
rownames(y) <- paste("Gene",1:100)
y[1:2,4:6] <- y[1:2,4:6] + 2
design <- cbind(Grp1=1,Grp2vs1=c(0,0,0,1,1,1))
options(digit=3)

# Ordinary fit
fit <- lmFit(y,design)
fit <- eBayes(fit)
fit
as.data.frame(fit[1:10,2])

# Various ways of summarising or plotting the results
topTable(fit,coef=2)
qqt(fit$t[,2],df=fit$df.residual+fit$df.prior)
abline(0,1)
volcanoplot(fit,coef=2,highlight=2)

# Various ways of writing results to file
## Not run: write.fit(fit,file="exampleresults.txt")
## Not run: write.table(fit,file="exampleresults2.txt")

# Robust fit
# (There may be some warning messages)
fit2 <- lmFit(y,design,method="robust")

# Fit with correlated arrays
# Suppose each pair of arrays is a block
block <- c(1,1,2,2,3,3)
dupcor <- duplicateCorrelation(y,design,block=block)
dupcor$consensus.correlation
fit3 <- lmFit(y,design,block=block,correlation=dupcor$consensus)

# Fit with duplicate probes
# Suppose two side-by-side duplicates of each gene
rownames(y) <- paste("Gene",rep(1:50,each=2))
dupcor <- duplicateCorrelation(y,design,ndups=2)
dupcor$consensus.correlation
fit4 <- lmFit(y,design,ndups=2,correlation=dupcor$consensus)
fit4 <- eBayes(fit3)
dim(fit4)
topTable(fit4,coef=2)



