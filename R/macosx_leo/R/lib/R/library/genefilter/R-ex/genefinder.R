### Name: genefinder
### Title: Finds genes that have similar patterns of expression.
### Aliases: genefinder genefinder,ExpressionSet,vector-method
###   genefinder,matrix,vector-method
### Keywords: manip

### ** Examples

set.seed(12345)

#create some fake expression profiles
m1 <- matrix (1:12, 4, 3)
v1 <- 1
nr <- 2

#find the 2 rows of m1 that are closest to row 1
genefinder (m1, v1, nr, method="euc")

v2 <- c(1,3)
genefinder (m1, v2, nr)

genefinder (m1, v2, nr, scale="range")

genefinder (m1, v2, nr, method="manhattan")

m2 <- matrix (rnorm(100), 10, 10)
v3 <- c(2, 5, 6, 8)
nr2 <- 6
genefinder (m2, v3, nr2, scale="zscore")

## Don't show:
        m1 <- matrix(rnorm(1000),100,10)
        v1 <- c(3,5,8,42)
        nr2 <- 35
        genefinder(m1,v1,nr2,method="euclidean")
        genefinder(m1,v1,nr2,method="maximum")
        genefinder(m1,v1,nr2,method="canberra")
        genefinder(m1,v1,nr2,method="binary")
        genefinder(m1,v1,nr2,method="correlation")
        
        m2 <- matrix(rnorm(10000),1000,10)
        v1 <- c(1,100,563,872,921,3,52,95,235,333)
        nr <- 100
        genefinder(m2,v1,nr2,scale="zscore",method="euclidean")
        genefinder(m2,v1,nr2,scale="range",method="maximum")
        genefinder(m2,v1,nr2,scale="zscore",method="canberra")
        genefinder(m2,v1,nr2,scale="range",method="binary")
        genefinder(m2,v1,nr2,scale="zscore",method="correlation")
        
## End Don't show



