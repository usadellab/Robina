###################################################
### chunk number 1: 
###################################################
library(affy)


###################################################
### chunk number 2: 
###################################################
print.probesloc.opt <- function(affy.opt, fields) {
  all.fields <- c("what", "where", "autoload", "repository", "installdir")
  
  if (sum (! (fields %in% all.fields)) > 0)
    stop(paste("'fields' can only contain elements of:",
         paste(all.fields, collapse=" ")))

  l <- lapply(affy.opt$probesloc, 
              function(x) x[fields])
  l <- lapply(l, function(x) {
                   unk <- is.na(names(x))
                   x[unk] <- rep(list(unk=NA), sum(unk))
                   x <- lapply(x, function(y) if (is.null(y)) "NULL"
                   else y )
                   return(x)
                 }
              )
  ul <-  as.character(unlist(l))
  m <- t(matrix(ul, nr=length(fields)))  
  colnames(m) <- fields
  print(m)
}


###################################################
### chunk number 3: 
###################################################
affy.opt <- getOption("BioC")$affy

print.probesloc.opt(affy.opt, c("what", "where", "autoload"))


###################################################
### chunk number 4: 
###################################################
deactivate.autoload <- function(affy.opt) {
  l <- lapply(affy.opt$probesloc, 
              function(x) {
                i <- names(x) == "autoload"
                x[i] <- list(FALSE)
                return(x)
              })
    affy.opt$probesloc <- l
    return(affy.opt)
  }           


###################################################
### chunk number 5: 
###################################################
affy.opt <- getOption("BioC")$affy

affy.opt.noauto <- deactivate.autoload(affy.opt)

# commit the changes
.setAffyOptions(affy.opt.noauto)


###################################################
### chunk number 6: 
###################################################
print.probesloc.opt(affy.opt, c("what", "autoload", "repository"))


###################################################
### chunk number 7: 
###################################################
my.installdir <- "mydir/is/here"
has.installdir <- unlist(lapply(affy.opt$probesloc, function(x) 
                                if("installdir"%in%names(x)) 
                                grep("installdir",names(x))
                                else numeric(0)
                                ))
l <- lapply(affy.opt$probesloc,
            function(x)  {
              if("installdir" %in% names(x)) {
                 x$installdir <- my.installdir
              } 
              return(x)
            })

affy.opt$probesloc <- l

.setAffyOptions(affy.opt)


###################################################
### chunk number 8: 
###################################################
data(affybatch.example)
dummymap.name <- "dummymap"
assign(dummymap.name, new.env())
affybatch.example@cdfName <- paste(dummymap.name, "cdf", sep="")


