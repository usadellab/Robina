### Name: createPackage
### Title: Create a Package Directory from a Template
### Aliases: createPackage
### Keywords: programming

### ** Examples

  sym  = list(AUTHOR = "Hesiod", VERSION = "1.0",
         TITLE = "the nine muses",
         FORMAT = "Character vector containg the names of the 9 muses.")

  res  = createPackage("muses",
           destinationDir = tempdir(),
           originDir      = system.file("Code", package="Biobase"),
           symbolValues   = sym,
           unlink = TRUE, quiet = FALSE)

  muses = c("Calliope", "Clio", "Erato", "Euterpe", "Melpomene",
            "Polyhymnia", "Terpsichore", "Thalia", "Urania")

  dir.create(file.path(res$pkgdir, "data"))

  save(muses, file = file.path(res$pkgdir, "data", "muses.rda"))

  res$pkgdir



