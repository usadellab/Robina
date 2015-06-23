### Name: lcSuffix
### Title: Compute the longest common prefix or suffix of a string
### Aliases: lcSuffix lcPrefix lcPrefixC
### Keywords: manip

### ** Examples

s1 <- c("ABC.CEL", "DEF.CEL")
lcSuffix(s1)

s2 <- c("ABC.123", "ABC.456")
lcPrefix(s2)

CHK <- stopifnot

CHK(".CEL" == lcSuffix(s1))
CHK("bc" == lcSuffix(c("abc", "333abc", "bc")))
CHK("c" == lcSuffix(c("c", "abc", "xxxc")))
CHK("" == lcSuffix(c("c", "abc", "xxx")))

CHK("ABC." == lcPrefix(s2))
CHK("ab" == lcPrefix(c("abcd", "abcd123", "ab", "abc", "abc333333")))
CHK("a" == lcPrefix(c("abcd", "abcd123", "ax")))
CHK("a" == lcPrefix(c("a", "abcd123", "ax")))
CHK("" == lcPrefix(c("a", "abc", "xxx")))

CHK("ab" == lcPrefixC(c("abcd", "abcd123", "ab", "abc", "abc333333")))
CHK("a" == lcPrefixC(c("abcd", "abcd123", "ax")))
CHK("a" == lcPrefixC(c("a", "abcd123", "ax")))
CHK("" == lcPrefixC(c("a", "abc", "xxx")))




