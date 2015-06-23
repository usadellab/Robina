### Name: AnnDbPkg-maker
### Title: Creates an SQLite-based annotation package
### Aliases: AnnDbPkg-maker AnnDbPkgSeed class:AnnDbPkgSeed
###   AnnDbPkgSeed-class makeAnnDbPkg makeAnnDbPkg,AnnDbPkgSeed-method
###   makeAnnDbPkg,list-method makeAnnDbPkg,character-method
###   loadAnnDbPkgIndex
### Keywords: utilities classes methods

### ** Examples

  ## With a "AnnDbPkgSeed" object:
  seed <- new("AnnDbPkgSeed",
      Package="hgu133a2.db",
      Version="0.0.99",
      PkgTemplate="HUMANCHIP.DB",
      AnnObjPrefix="hgu133a2"
  )
  if (FALSE)
      makeAnnDbPkg(seed, "path/to/hgu133a2.sqlite")

  ## With package names:
  ## (Note that in this case makeAnnDbPkg() will use the package descriptions
  ## found in the master index file ANNDBPKG-INDEX.TXT located in the
  ## AnnotationDbi package.)
  if (FALSE)
      makeAnnDbPkg(c("hgu95av2.db", "hgu133a2.db"))

  ## A character vector of length 1 is treated as a regular expression:
  if (FALSE)
      makeAnnDbPkg("hgu.*")
  ## To make all the packages described in the master index:
  if (FALSE)
      makeAnnDbPkg("")
  ## Extra args can be used to narrow down the roaster of packages to make:
  if (FALSE) {
      makeAnnDbPkg("", PkgTemplate="HUMANCHIP.DB", manufacturer="Affymetrix")
      makeAnnDbPkg(".*[3k]\\.db", species=c("Mouse", "Rat"))
  }

  ## The master index file ANNDBPKG-INDEX.TXT can be loaded with:
  loadAnnDbPkgIndex()



