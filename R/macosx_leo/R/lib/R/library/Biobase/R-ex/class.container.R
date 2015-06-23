### Name: container
### Title: A Lockable List Structure with Constraints on Content
### Aliases: class:container container container-class [,container-method
###   [[<-,container-method [[,container-method content,container-method
###   length,container-method locked,container-method show,container-method
### Keywords: methods classes

### ** Examples

  x1 <- new("container", x=vector("list", length=3), content="lm")
  lm1 <- lm(rnorm(10)~runif(10))
  x1[[1]] <- lm1
 


