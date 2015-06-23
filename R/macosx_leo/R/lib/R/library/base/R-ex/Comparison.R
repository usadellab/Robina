### Name: Comparison
### Title: Relational Operators
### Aliases: < <= == != >= > Comparison
### Keywords: logic

### ** Examples

x <- stats::rnorm(20)
x < 1
x[x > 0]

x1 <- 0.5 - 0.3
x2 <- 0.3 - 0.1
x1 == x2                           # FALSE on most machines
identical(all.equal(x1, x2), TRUE) # TRUE everywhere



