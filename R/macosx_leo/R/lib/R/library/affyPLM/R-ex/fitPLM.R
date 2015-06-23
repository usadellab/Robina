### Name: fitPLM
### Title: Fit a Probe Level Model to Affymetrix Genechip Data.
### Aliases: fitPLM
### Keywords: manip

### ** Examples

data(affybatch.example)
Pset <- fitPLM(affybatch.example,model=PM ~ -1 + probes + samples)
se(Pset)[1:5,]

# A larger example testing weight image function
data(Dilution)
## Not run: Pset <- fitPLM(Dilution,model=PM ~ -1 + probes + samples)
## Not run: image(Pset)
## Not run: NUSE(Pset) # NUSE

#now lets try a wider class of models
## Not run: Pset <- fitPLM(Dilution,model=PM ~ -1 + probes +liver,normalize=FALSE,background=FALSE)
## Not run: coefs(Pset)[1:10,]

## Not run: Pset <- fitPLM(Dilution,model=PM ~ -1 + probes + liver + scanner,normalize=FALSE,background=FALSE)
coefs(Pset)[1:10,]

#try liver as a covariate
logliver <- log2(c(20,20,10,10))
## Not run: Pset <- fitPLM(Dilution,model=PM~-1+probes+logliver+scanner,normalize=FALSE,background=FALSE,variable.type=c(logliver="covariate"))
coefs(Pset)[1:10,]

#try a different se.type
## Not run: Pset <- fitPLM(Dilution,model=PM~-1+probes+scanner,normalize=FALSE,background=FALSE,model.param=list(se.type=2))
se(Pset)[1:10,]




