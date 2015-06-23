### Name: read.maimages
### Title: Read RGList from Image Analysis Output Files
### Aliases: read.maimages read.imagene
### Keywords: file

### ** Examples

#  Read all .gpr files from current working directory
#  and give weight 0.1 to spots with negative flags

## Not run: 
##D files <- dir(pattern="*\\.gpr$")
##D RG <- read.maimages(files,"genepix",wt.fun=wtflags(0.1))
## End(Not run)

#  Read all .spot files from current working director and down-weight
#  spots smaller or larger than 150 pixels

## Not run: 
##D files <- dir(pattern="*\\.spot$")
##D RG <- read.maimages(files,"spot",wt.fun=wtarea(150))
## End(Not run)



