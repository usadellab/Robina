### Name: Aggregate
### Title: A Simple Aggregation Mechanism.
### Aliases: Aggregate
### Keywords: programming methods

### ** Examples

  agg1 <- new("aggregator")
  Aggregate(letters[1:10], agg1)
  # the first 10 letters should be symbols in env1 with values of 1
  Aggregate(letters[5:11], agg1)
  # now letters[5:10] should have value 2
  bb <- mget(letters[1:11], env=aggenv(agg1), ifnotfound=NA)
  t1 <- as.numeric(bb); names(t1) <- names(bb)
  t1
# a b c d e f g h i j k
# 1 1 1 1 2 2 2 2 2 2 1



