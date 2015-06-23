### Name: popYEASTCHIPDB
### Title: Populates an SQLite DB with and produces a schema definition
### Aliases: popYEASTCHIPDB
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
##D   ##Builds the ag sqlite:
##D   popYEASTCHIPDB(affy=TRUE,
##D                  fileName="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/yeast2/Yeast_2_annot.csv.070824",
##D                  prefix="yeast2",
##D                  chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_yeast.sqlite",
##D                  metaDataSrc = my_metaDataSrc,
##D                  printSchema=TRUE)
##D 
##D   ##Or if the package is a standard package (it probably isn't):
##D   popYEASTCHIPDB(affy=TRUE,
##D                  fileName="/mnt/cpb_anno/mcarlson/proj/sqliteGen/srcFiles/yeast2/Yeast_2_annot.csv.070824",
##D                  prefix="yeast2",
##D                  chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_yeast.sqlite",
##D                  metaDataSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/metadatasrc.sqlite",
##D                  printSchema=TRUE)
## End(Not run)



