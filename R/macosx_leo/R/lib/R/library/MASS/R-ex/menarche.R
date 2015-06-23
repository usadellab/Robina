### Name: menarche
### Title: Age of Menarche data
### Aliases: menarche
### Keywords: datasets

### ** Examples

mprob <- glm(cbind(Menarche, Total - Menarche) ~ Age,
             binomial(link = probit), data = menarche)



