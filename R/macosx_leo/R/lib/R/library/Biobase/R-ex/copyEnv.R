### Name: copyEnv
### Title: List-Environment interactions
### Aliases: copyEnv l2e
### Keywords: utilities

### ** Examples

   z <- new.env(hash=TRUE, parent=emptyenv(), size=29L)
   multiassign(c("a","b","c"), c(1,2,3), z)

   a <- copyEnv(z)
   ls(a)

   q <- as.list(z)
   g <- new.env(hash=TRUE, parent=emptyenv(), size=29L)
   g <- l2e(q, g)
   ls(g)
   g2 <- l2e(q)



