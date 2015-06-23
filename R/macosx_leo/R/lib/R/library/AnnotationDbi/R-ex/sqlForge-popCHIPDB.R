### Name: popHUMANCHIPDB
### Title: Populates an SQLite DB with and produces a schema definition
### Aliases: popHUMANCHIPDB popMOUSECHIPDB popRATCHIPDB popFLYCHIPDB
### Keywords: utilities

### ** Examples

## Not run: 
##D   ##Set up the metadata
##D   my_metaDataSrc <- c( DBSCHEMA="the DB schema",
##D                     ORGANISM="the organism",
##D                     SPECIES="the species",
##D                     MANUFACTURER="the manufacturer",
##D                     CHIPNAME="the chipName",
##D                     MANUFACTURERURL="the manufacturerUrl")  
##D 
##D   ##Builds the org.Hs.eg sqlite:
##D   popHUMANCHIPDB(affy=TRUE,
##D                  prefix="hgu95av2",
##D                  fileName="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/HG_U95Av2_annot.csv.070824",
##D                  chipMapSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipmapsrc_human.sqlite",
##D                  chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_human.sqlite",
##D                  metaDataSrc = my_metaDataSrc,
##D                  otherSrc=c(
##D                    EA="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2.EA.txt",
##D                    UMICH="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2_UMICH.txt"),
##D                  printSchema=TRUE)
##D 
##D   ##Or if the package is a standard package (it probably isn't):
##D   popHUMANCHIPDB(affy=TRUE,
##D                  prefix="hgu95av2",
##D                  fileName="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/HG_U95Av2_annot.csv.070824",
##D                  chipMapSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipmapsrc_human.sqlite",
##D                  chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_human.sqlite",
##D                  metaDataSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/metadatasrc.sqlite",
##D                  otherSrc=c(
##D                    EA="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2.EA.txt",
##D                    UMICH="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/hgu95av2/hgu95av2_UMICH.txt"),
##D                  printSchema=TRUE)
## End(Not run)



