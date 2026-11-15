package fleet.com.jetbrains.lang.syntax

typealias CharOffset = Long

interface SyntaxNode {
  val type: Any
  val text: CharSequence
  val startOffset: CharOffset
  val endOffset: CharOffset
  val errorMessage: String?
  val language: Any? //todo so far we expect it to be LanguageId, need a proper place probably

  fun parent(): SyntaxNode?

  fun firstChild(): SyntaxNode?
  fun lastChild(): SyntaxNode?

  fun nextSibling(): SyntaxNode?
  fun prevSibling(): SyntaxNode?

  fun childByOffset(offset: CharOffset): SyntaxNode?
}
