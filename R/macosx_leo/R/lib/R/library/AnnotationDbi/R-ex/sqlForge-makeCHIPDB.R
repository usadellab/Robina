### Name: makeHUMANCHIP_DB
### Title: Creates a sqlite database, and then makes an annotation package
###   with it
### Aliases: makeHUMANCHIP_DB makeMOUSECHIP_DB makeRATCHIP_DB
###   makeFLYCHIP_DB
### Keywords: utilities

### ** Examples

## Not run: 
##D makeHUMANCHIP_DB(affy = TRUE,
##D                  prefix = "hgu95av2",
##D                  fileName = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/HG_U95Av2_annot.csv.070824",
##D                  otherSrc = c(
##D                    EA="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2.EA.txt",
##D                    UMICH="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2_UMICH.txt"),
##D                  chipMapSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipmapsrc_human.sqlite",
##D                  chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_human.sqlite",
##D                  baseMapType = "gbNRef",
##D                  version = "1.0.0",
##D                  manufacturer = "Affymetrix",
##D                  chipName = "hgu95av2",
##D                  manufacturerUrl = "http://www.affymetrix.com")
## End(Not run)



