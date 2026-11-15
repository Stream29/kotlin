package com.intellij.platform.syntax.impl.lexer

import com.intellij.platform.syntax.SyntaxElementType
import com.intellij.platform.syntax.lexer.TokenList

class TokenListBuilder internal constructor(private val text: CharSequence) {
    private data class Token(val start: Int, val end: Int, val type: SyntaxElementType)

    private val tokens = mutableListOf<Token>()

    fun token(
        start: Int,
        end: Int,
        type: SyntaxElementType,
    ) {
        tokens.add(Token(start, end, type))
    }

    internal fun build(): TokenList {
        val lexStarts = IntArray(tokens.size + 1) {
            if (it == tokens.size) tokens.last().end else tokens[it].start
        }
        val lexTypes = Array(tokens.size + 1) {
            tokens.getOrNull(it)?.type
        }
        return TokenList(
            lexStarts, lexTypes as Array<SyntaxElementType>, tokens.size, text
        )
    }
}

fun CharSequence.toTokenList(builderFunc: TokenListBuilder.() -> Unit): TokenList =
    TokenListBuilder(this).apply(builderFunc).build()