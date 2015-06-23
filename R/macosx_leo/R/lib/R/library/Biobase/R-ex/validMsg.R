### Name: validMsg
### Title: Conditionally append result to validity message
### Aliases: validMsg
### Keywords: utilities

### ** Examples

msg <- NULL
validMsg(msg, FALSE) # still NULL
msg <- validMsg(msg, "one")
validMsg(msg, "two")



