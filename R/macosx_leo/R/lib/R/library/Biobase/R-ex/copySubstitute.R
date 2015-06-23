### Name: copySubstitute
### Title: Copy Between Connections or Files with Configure-Like Name-Value
###   Substitution
### Aliases: copySubstitute
### Keywords: connection programming

### ** Examples

## create an example file
infile  = tempfile()
outfile = tempfile()

writeLines(text=c("We will perform in @WHAT@:",
  "So, thanks to @WHOM@ at once and to each one,",
  "Whom we invite to see us crown'd at @WHERE@."),
  con = infile)

## create the symbol table
z = list(WHAT="measure, time and place", WHOM="all", WHERE="Scone")

## run copySubstitute
copySubstitute(infile, outfile, z)

## display the results
readLines(outfile)


##--------------------------------------------------------------
## This is a slightly more complicated example that demonstrates
## how copySubstitute works on nested directories
##--------------------------------------------------------------
d = tempdir()
my.dir.create = function(x) {dir.create(x); return(x)}

unlink(file.path(d, "src"), recursive=TRUE)
unlink(file.path(d, "dest"), recursive=TRUE)

## create some directories and files:
src  = my.dir.create(file.path(d, "src"))
dest = file.path(d, "dest")
d1   = my.dir.create(file.path(src, "dir1.in"))
d2   = my.dir.create(file.path(src, "dir2@FOO@.in"))
d3   = my.dir.create(file.path(d2, "dir3"))
d4   = my.dir.create(file.path(d3, "dir4"))
d5   = my.dir.create(file.path(d4, "dir5@BAR@"))
writeLines(c("File1:", "FOO: @FOO@"),     file.path(d1, "file1.txt.in"))
writeLines(c("File2:", "BAR: @BAR@"),     file.path(d2, "file2.txt.in"))
writeLines(c("File3:", "SUN: @SUN@"),     file.path(d3, "file3.txt.in"))
writeLines(c("File4:", "MOON: @MOON@"),   file.path(d4, "@SUN@.txt"))

## call copySubstitute
copySubstitute(src, dest, recursive=TRUE,
               symbolValues = list(FOO="thefoo", BAR="thebar",
                                   SUN="thesun", MOON="themoon"))

## view the result
listsrc  = dir(src,  full.names=TRUE, recursive=TRUE)
listdest = dir(dest, full.names=TRUE, recursive=TRUE)
listsrc
listdest

cat(unlist(lapply(listsrc,  readLines)), sep="\n")
cat(unlist(lapply(listdest, readLines)), sep="\n")



