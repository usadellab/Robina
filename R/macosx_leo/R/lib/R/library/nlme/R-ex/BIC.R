### Name: BIC
### Title: Bayesian Information Criterion
### Aliases: BIC BIC.lm BIC.lmList BIC.gls BIC.lme BIC.nls BIC.nlsList
###   BIC.nlme
### Keywords: models

### ** Examples

fm1 <- lm(distance ~ age, data = Orthodont) # no random effects
BIC(fm1)
fm2 <- lme(distance ~ age, data = Orthodont) # random is ~age
BIC(fm1, fm2)



