### Name: pm.titles
### Title: Obtain the titles of the PubMed abstracts.
### Aliases: pm.titles
### Keywords: manip

### ** Examples

  library("hgu95av2.db")
  hoxa9 <- "37809_at"
  absts <- pm.getabst(hoxa9, "hgu95av2")
  pm.titles(absts)



