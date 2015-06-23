### Name: addVigs2WinMenu
### Title: Add Menu Items to an Existing/New Menu of Window
### Aliases: addVigs2WinMenu
### Keywords: interface

### ** Examples

    # Only works for windows now
    if(interactive() && .Platform$OS.type == "windows" &&
             .Platform$GUI == "Rgui"){
        addVigs2WinMenu("Biobase")
    }



