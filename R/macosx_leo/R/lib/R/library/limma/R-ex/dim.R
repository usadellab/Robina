### Name: dim
### Title: Retrieve the Dimensions of an RGList, MAList or MArrayLM Object
### Aliases: dim.RGList dim.MAList dim.MArrayLM length.RGList length.MAList
###   length.MArrayLM
### Keywords: array

### ** Examples

M <- A <- matrix(11:14,4,2)
rownames(M) <- rownames(A) <- c("a","b","c","d")
colnames(M) <- colnames(A) <- c("A1","A2")
MA <- new("MAList",list(M=M,A=A))
dim(M)
ncol(M)
nrow(M)
length(M)



