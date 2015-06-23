### Name: is.fullrank
### Title: Check for Full Column Rank
### Aliases: is.fullrank nonEstimable
### Keywords: algebra

### ** Examples

# TRUE
is.fullrank(1)
is.fullrank(cbind(1,0:1))

# FALSE
is.fullrank(0)
is.fullrank(matrix(1,2,2))
nonEstimable(matrix(1,2,2))



