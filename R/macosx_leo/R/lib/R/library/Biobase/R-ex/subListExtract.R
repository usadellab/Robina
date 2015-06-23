### Name: subListExtract
### Title: Extract the same element from the sublists of a list
### Aliases: subListExtract
### Keywords: manip

### ** Examples

list_size = 500000
innerL = list(foo="foo", bar="bar")
L = rep(list(innerL), list_size)

system.time({j0 = sapply(L, function(x) x$foo)})
system.time({j1 = subListExtract(L, "foo", simplify=TRUE)})
stopifnot(all.equal(j0, j1))

LS = L[1:3]
names(LS) = LETTERS[1:3]
subListExtract(LS, "bar", simplify=TRUE)
subListExtract(LS, "bar", simplify=FALSE)
subListExtract(LS, "bar", simplify=TRUE, keep.names=FALSE)



