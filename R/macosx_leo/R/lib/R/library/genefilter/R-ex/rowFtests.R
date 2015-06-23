### Name: rowFtests
### Title: t-tests and F-tests for rows or columns of a matrix
### Aliases: rowFtests rowFtests,matrix,factor-method
###   rowFtests,ExpressionSet,factor-method
###   rowFtests,ExpressionSet,character-method colFtests
###   colFtests,matrix,factor-method colFtests,ExpressionSet,factor-method
###   colFtests,ExpressionSet,character-method rowttests
###   rowttests,matrix,factor-method rowttests,matrix,missing-method
###   rowttests,ExpressionSet,factor-method
###   rowttests,ExpressionSet,character-method
###   rowttests,ExpressionSet,missing-method colttests
###   colttests,matrix,factor-method colttests,matrix,missing-method
###   colttests,ExpressionSet,factor-method
###   colttests,ExpressionSet,character-method
###   colttests,ExpressionSet,missing-method fastT
### Keywords: math

### ** Examples

   x  = matrix(runif(970), ncol=97)
   f2 = factor(floor(runif(ncol(x))*2))
   f7 = factor(floor(runif(ncol(x))*7))

   r1 = rowttests(x)
   r2 = rowttests(x, f2)
   r7 = rowFtests(x, f7)

   ## compare with pedestrian tests
   about.equal = function(x,y,tol=1e-10)
     stopifnot(all(abs(x-y) < tol))

   for (j in 1:nrow(x)) {
     s1 = t.test(x[j,])
     about.equal(s1$statistic, r1$statistic[j])
     about.equal(s1$p.value,   r1$p.value[j])

     s2 = t.test(x[j,] ~ f2, var.equal=TRUE)
     about.equal(s2$statistic, r2$statistic[j])
     about.equal(s2$p.value,   r2$p.value[j])

     dm = -diff(tapply(x[j,], f2, mean))
     about.equal(dm, r2$dm[j])

     s7 = summary(lm(x[j,]~f7))
     about.equal(s7$statistic$value, r7$statistic[j])
   }

   ## colttests
   c2 = colttests(t(x), f2)
   stopifnot(identical(r2, c2))
 
   ## missing values
   f2n = f2
   f2n[sample(length(f2n), 3)] = NA
   r2n = rowttests(x, f2n)
   for(j in 1:nrow(x)) {
     s2n = t.test(x[j,] ~ f2n, var.equal=TRUE)
     about.equal(s2n$statistic, r2n$statistic[j])
     about.equal(s2n$p.value,   r2n$p.value[j])
  }




