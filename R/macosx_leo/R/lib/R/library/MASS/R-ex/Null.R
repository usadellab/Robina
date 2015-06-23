### Name: Null
### Title: Null Spaces of Matrices
### Aliases: Null
### Keywords: algebra

### ** Examples

# The function is currently defined as
function(M)
{
        tmp <- qr(M)
        set <- if(tmp$rank == 0) 1:ncol(M) else  - (1:tmp$rank)
        qr.Q(tmp, complete = TRUE)[, set, drop = FALSE]
}



