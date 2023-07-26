package com.sap.ase.poker.model.deck;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CardTest {

    @Test
    void kingIsGreaterThanSeven() {
        Card king = new Card(Kind.KING, Suit.DIAMONDS);
        Card seven = new Card(Kind.SEVEN, Suit.DIAMONDS);

        assertThat(king.compareTo(seven)).isPositive();
    }

    @Test
    void kingsAreEven() {
        Card kingDiamond = new Card(Kind.KING, Suit.DIAMONDS);
        Card kingHeart = new Card(Kind.KING, Suit.HEARTS);

        assertThat(kingDiamond.compareTo(kingHeart)).isZero();
    }

    @Test
    void sevenIsSmallerThanKing() {
        Card seven = new Card(Kind.SEVEN, Suit.DIAMONDS);
        Card king = new Card(Kind.KING, Suit.DIAMONDS);

        assertThat(seven.compareTo(king)).isNegative();
    }

}