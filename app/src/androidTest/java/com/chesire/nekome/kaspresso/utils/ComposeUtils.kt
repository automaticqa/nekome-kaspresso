package com.chesire.nekome.kaspresso.utils

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasAnyAncestor
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.ComposeTestRule
import com.kaspersky.components.composesupport.core.KNode
import io.github.kakaocup.compose.node.core.BaseNode

fun assertNodeWithTextAndAncestorDisplayed(
    composeTestRule: ComposeTestRule,
    text: String,
    ancestorTestTag: String
) {
    composeTestRule.onNode(
        hasText(text)
            .and(hasAnyAncestor(hasTestTag(ancestorTestTag)))
    ).assertIsDisplayed()
}

fun BaseNode<*>.assertTextIsDisplayedInCompose(text: String) {
    child<KNode> {
        hasText(text, ignoreCase = true)
    }.assertIsDisplayed()
}

fun BaseNode<*>.clickItemWithTitle(title: String) {
    child<KNode> {
        hasText(title)
    }.performClick()
}