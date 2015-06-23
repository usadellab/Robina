### Name: popHUMANDB
### Title: Populates an SQLite DB with and produces a schema definition
### Aliases: popHUMANDB popMALARIADB popMOUSEDB popRATDB popFLYDB
###   popYEASTDB
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
##D   popHUMANDB(prefix="org.Hs.eg",
##D              chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_human.sqlite",
##D              metaDataSrc = my_metaDataSrc,
##D              printSchema=TRUE)
##D 
##D   ##Or if the package is a standard package (it probably isn't):
##D   ##Builds the org.Hs.eg sqlite:
##D   popHUMANDB(prefix="org.Hs.eg",
##D              chipSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/chipsrc_human.sqlite",
##D              metaDataSrc = "/mnt/cpb_anno/mcarlson/proj/sqliteGen/nli/annosrc/db/metadatasrc.sqlite",
##D              printSchema=TRUE)
## End(Not run)



