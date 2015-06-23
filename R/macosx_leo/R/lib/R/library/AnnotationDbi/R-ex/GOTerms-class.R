### Name: GOTerms-class
### Title: Class "GOTerms"
### Aliases: class:GOTerms GOTerms-class GOTerms initialize,GOTerms-method
###   GOID GOID,GOTerms-method Term Term,GOTerms-method Ontology
###   Ontology,GOTerms-method Definition Definition,GOTerms-method Synonym
###   Synonym,GOTerms-method Secondary Secondary,GOTerms-method
###   show,GOTerms-method
### Keywords: methods classes

### ** Examples

  gonode <- new("GOTerms", GOID="GO:1234567", Term="Test", Ontology="MF",
                          Definition="just for testing")
  GOID(gonode)
  Term(gonode)
  Ontology(gonode)



