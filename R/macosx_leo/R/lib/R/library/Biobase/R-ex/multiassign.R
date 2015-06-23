### Name: multiassign
### Title: Assign Values to a Names
### Aliases: multiassign
### Keywords: data

### ** Examples

#-- Create objects  'r1', 'r2', ... 'r6' --
nam <- paste("r",1:6, sep=".")

multiassign(nam, 11:16)
ls(pat="^r..$")

#assign the values in y to variables with the names from y

y<-list(a=4,d=mean,c="aaa")
multiassign(y)




