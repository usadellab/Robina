### Name: RSiteSearch
### Title: Search for Key Words or Phrases in the R-help Mailing List
###   Archives or Documentation
### Aliases: RSiteSearch
### Keywords: utilities documentation

### ** Examples
## Not run: 
##D  # need Internet connection
##D RSiteSearch("{logistic regression}") # matches exact phrase
##D RSiteSearch("Baron Liaw", restr = "Rhelp02a")
##D ## Search in R-devel archive and documents  (and store the query-string):
##D fullquery <- RSiteSearch("S4", restr = c("R-dev", "docs"))
##D fullquery # a string of ~ 116 characters
##D ## the latest purported bug reports
##D RSiteSearch("bug", restr = "R-devel", sortby = "date:late")
## End(Not run)


