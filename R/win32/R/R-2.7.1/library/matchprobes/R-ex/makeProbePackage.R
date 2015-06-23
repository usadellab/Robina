### Name: makeProbePackage
### Title: Make a package with probe sequence related data for microarrays
### Aliases: makeProbePackage
### Keywords: IO utilities

### ** Examples

filename <- system.file("extdata", "HG-U95Av2_probe_tab.gz", 
     package="matchprobes")
outdir   <- tempdir()
me       <- "Wolfgang Huber <huber@ebi.ac.uk>"
makeProbePackage("HG-U95Av2",
                 datafile   = gzfile(filename, open="r"),
                 outdir     = outdir,
                 maintainer = me, 
                 version    = "0.0.1",
                 species    = "Homo_sapiens", 
                 check      = FALSE,
                 force      = TRUE)
dir(outdir)



