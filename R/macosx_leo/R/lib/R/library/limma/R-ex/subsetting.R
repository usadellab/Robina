### Name: subsetting
### Title: Subset RGList, MAList or MArrayLM Objects
### Aliases: [.RGList [.MAList [.MArrayLM
### Keywords: manip

### ** Examples

M <- A <- matrix(11:14,4,2)
rownames(M) <- rownames(A) <- c("a","b","c","d")
colnames(M) <- colnames(A) <- c("A","B")
MA <- new("MAList",list(M=M,A=A))
MA[1:2,]
MA[1:2,2]
MA[,2]



