### Name: makeDataPackage
### Title: Make an R package from a data object
### Aliases: makeDataPackage makeDataPackage,ANY-method
### Keywords: manip

### ** Examples

 data(sample.ExpressionSet)
 ## package created in tempdir()
 s1 <- makeDataPackage(sample.ExpressionSet,
                       author = "Foo Author",
                       email = "foo@bar",
                       packageName = "FooBarPkg",
                       packageVersion = "1.0.0")



