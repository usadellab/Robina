### Name: getGraphicsEvent
### Title: Wait for a mouse or keyboard event from a graphics window
### Aliases: getGraphicsEvent
### Keywords: iplot

### ** Examples

## Not run: 
##D     mousedown <- function(buttons, x, y) {
##D         cat("Buttons ", paste(buttons, collapse=" "), " at ", x, y, "\n")
##D         points(x, y)
##D         if (x > 0.85 && y > 0.85) "Done"
##D         else NULL
##D     }
##D     
##D     mousemove <- function(buttons, x, y) {
##D         points(x, y)
##D         NULL
##D     }
##D     
##D     keybd <- function(key) {
##D         cat("Key <", key, ">\n", sep = "")
##D     }
##D     
##D     plot(0:1, 0:1, type='n')
##D     getGraphicsEvent("Click on upper right to quit",
##D                      onMouseDown = mousedown,
##D                      onMouseMove = mousemove,
##D                      onKeybd = keybd)
## End(Not run)



