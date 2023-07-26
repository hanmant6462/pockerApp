package com.sap.ase.poker.service;

import com.sap.ase.poker.model.GameState;
import com.sap.ase.poker.model.IllegalActionException;
import com.sap.ase.poker.model.IllegalAmountException;
import com.sap.ase.poker.model.Player;
import com.sap.ase.poker.model.deck.Card;
import com.sap.ase.poker.model.deck.Deck;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.util.ReflectionTestUtils;


public class TableServiceTest {

    private TableService tableService;

    Supplier<Deck> deckSupplier;
    Deck deck;
    Card card;

    @BeforeEach
    public void setup() {
        deckSupplier = Mockito.mock(Supplier.class);
        deck = Mockito.mock(Deck.class);
        card = Mockito.mock(Card.class);
        tableService = new TableService(deckSupplier);
        Mockito.when(deckSupplier.get()).thenReturn(deck);
        Mockito.when(deck.draw()).thenReturn(card);
    }

    @Test
    public void when_zero_expect_zero_getPlayers() {
        List<Player> players;
        players = tableService.getPlayers();
        assertEquals(0, players.size());
    }

    @Test
    public void when_one_Expect_One_getPlayers() {
        tableService.addPlayer("1", "Hanmant");
        List<Player> players = tableService.getPlayers();
        assertEquals(1, players.size());
    }

    @Test
    public void when_gamenotstarted_expect_null_getCurrentPlayer() {
        Optional<Player> currentPlayer = tableService.getCurrentPlayer();
        assertEquals(false, currentPlayer.isPresent());

    }

    @Test
    public void when_gamenstarted_expect_current_player_getCurrentPlayer() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        Optional<Player> currentPlayer = tableService.getCurrentPlayer();
        assertEquals("1", currentPlayer.get().getId());

    }

    @Test
    public void when_gamenotstarted_expect_open_getState() {
        GameState gamestate = tableService.getState();
        assertEquals(GameState.OPEN, gamestate);

    }

    @Test
    public void when_gamestarted_expect_preflop_getState() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "piyush");
        tableService.start();
        GameState gamestate = tableService.getState();
        assertEquals(GameState.PRE_FLOP, gamestate);
    }

    @Test
    public void when_player_joins_expect_100_cash_addPlayer() {
        tableService.addPlayer("1", "Hanmant");
        List<Player> players = tableService.getPlayers();
        assertEquals(1, players.size());
        Player player = players.get(0);
        assertEquals(100, player.getCash());
        assertEquals(false, player.isActive());
    }

    @Test
    public void when_start_with_one_player_expect_exception() {
        boolean exceptionFound = false;
        try {
            tableService.addPlayer("1", "Hanmant");
            tableService.start();
        } catch (IllegalActionException iae) {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);

    }

    @Test
    public void when_start_with_two_players_and_status_changesTo_Preflop() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        GameState gameState = tableService.getState();
        assertEquals(GameState.PRE_FLOP.getValue(), gameState.getValue());
        List<Player> players = tableService.getPlayers();
        for (int i = 0; i < players.size(); i++) {
            assertEquals(true, players.get(0).isActive());
            if (i == 0) {
                Optional<Player> currentPlayer = tableService.getCurrentPlayer();
                assertEquals(players.get(0).getId(), currentPlayer.get().getId());
                assertEquals(players.get(0).getName(), currentPlayer.get().getName());
            }
        }
    }

    @Test
    public void when_pre_flop_expected_empty_list() {
        ReflectionTestUtils.setField(tableService, "state", GameState.PRE_FLOP);
        List<Card> communityCards = tableService.getCommunityCards();
        assertNotNull(communityCards);
        assertEquals(0, communityCards.size());
    }


    @Test
    public void when_post_flop_expect_three_card_list() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.FLOP, tableService.getState());
        List<Card> communityCards = tableService.getCommunityCards();
        assertEquals(3, communityCards.size());
    }

    @Test
    public void when_post_turn_expect_four_card_list() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.FLOP, tableService.getState());
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.TURN, tableService.getState());
        List<Card> communityCards = tableService.getCommunityCards();
        assertEquals(4, communityCards.size());
    }

    @Test
    public void when_post_river_expect_five_card_list() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.FLOP, tableService.getState());
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.TURN, tableService.getState());
        tableService.performAction("check", 0);
        tableService.performAction("check", 0);
        assertEquals(GameState.RIVER, tableService.getState());
        List<Card> communityCards = tableService.getCommunityCards();
        assertEquals(5, communityCards.size());
    }

    @Test
    public void when_game_not_started_playerCards_empty() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        List<Card> cards = tableService.getPlayerCards("1");
        assertEquals(0, cards.size());

    }

    @Test
    public void when_game_started_playerCards_two() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        List<Card> cards = tableService.getPlayerCards("1");
        assertEquals(2, cards.size());

    }

    @Test
    public void when_check_performed_by_not_last_player() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "Akash");
        tableService.start();
        Optional currentPlayer = tableService.getCurrentPlayer();
        assertEquals("1", ((Player) currentPlayer.get()).getId());
        tableService.performAction("check", 0);
        currentPlayer = tableService.getCurrentPlayer();
        assertEquals("2", ((Player) currentPlayer.get()).getId());

    }

    @Test
    public void when_check_performed_after_bet_player() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "Akash");
        tableService.start();
        tableService.performAction("raise", 20);
        IllegalActionException thrown = assertThrows(
                IllegalActionException.class,
                () -> tableService.performAction("check",0),
                "Invalid case"
        );
        assertTrue(thrown.getMessage().contains("Invalid case"));

    }
    @Test
    public void when_check_performed_by_last_player() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "Akash");
        tableService.start();
        Optional currentPlayer = tableService.getCurrentPlayer();
        assertEquals("1", ((Player) currentPlayer.get()).getId());
        tableService.performAction("check", 0);
        currentPlayer = tableService.getCurrentPlayer();
        assertEquals("2", ((Player) currentPlayer.get()).getId());
        tableService.performAction("check", 0);
        currentPlayer = tableService.getCurrentPlayer();
        assertEquals("3", ((Player) currentPlayer.get()).getId());
        tableService.performAction("check", 0);
        GameState state = tableService.getState();
        assertEquals(GameState.FLOP, state);
    }

    @Test
    public void when_fold_performed_by_first_player() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "Akash");
        tableService.start();
        Optional currentPlayer = tableService.getCurrentPlayer();
        assertEquals("1", ((Player) currentPlayer.get()).getId());
        tableService.performAction("fold", 0);
        List<Player> players = tableService.getPlayers();
        boolean playerFound = false;
        for (Player player : players) {
            if (player.getId() == ((Player) currentPlayer.get()).getId()) {
                playerFound = true;
                if (player.isActive()) {
                    assertTrue(false, "player is Active after fold");
                }
                if (player.getCash() != ((Player) currentPlayer.get()).getCash()) {
                    assertTrue(false, "player cash has changed");
                }

            }
        }
        assertTrue(playerFound);
    }


    @Test
    public void when_fold_performed_by_last_remaining_player() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        Optional currentPlayer = tableService.getCurrentPlayer();
        assertEquals("1", ((Player) currentPlayer.get()).getId());
        tableService.performAction("fold", 0);
        Player player = tableService.getWinner().get();
        assertEquals("2", player.getId());
        assertEquals(GameState.ENDED, tableService.getState());

    }


    @Test
    public void when_raise_performed_by_player_Throw_Exception_illigalAmt() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();
        boolean exceptionFound = false;
        try {
            tableService.performAction("raise", 120);
        } catch (IllegalAmountException iae) {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }



    /*@Test
    public void when_raise_performed_by_player_with_same_bet_amt() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.start();

        boolean exceptionFound = false;
        try {
            //tableService.getCurrentPlayer().get().bet(10);
            tableService.performAction("raise", 10);
            tableService.performAction("call", -1);
        } catch (IllegalAmountException iae) {
            exceptionFound = true;
        }
        assertTrue(exceptionFound);
    }*/



    @Test
    public void when_Call_performed_by_player_after_Raise_ExpectMaxBet_to_be_deducted() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "panneer");
        tableService.start();
        tableService.performAction("raise", 30);
        tableService.performAction("call", -1);
        assertEquals(70, tableService.getPlayers().get(1).getCash());
    }


    @Test
    public void getMaxBetTest() {
        tableService.addPlayer("1", "Hanmant");
        tableService.addPlayer("2", "Piyush");
        tableService.addPlayer("3", "panneer");
        tableService.start();
        tableService.performAction("raise", 20);
        tableService.performAction("raise", 60);
        assertEquals(60, tableService.getMaxBet());
    }


}