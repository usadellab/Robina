### Name: matchpt
### Title: Nearest neighbor search.
### Aliases: matchpt
### Keywords: manip array

### ** Examples

    a <- matrix(c(2,2,3,5,1,8,-1,4,5,6), ncol=2L, nrow=5L)
    rownames(a) = LETTERS[seq_len(nrow(a))]
    matchpt(a)
    b <- c(1,2,4,5,6)
    d <- c(5.3, 3.2, 8.9, 1.3, 5.6, -6, 4.45, 3.32)
    matchpt(b, d)
    matchpt(d, b)



