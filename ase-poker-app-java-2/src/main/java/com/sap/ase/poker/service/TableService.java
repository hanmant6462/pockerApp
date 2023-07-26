package com.sap.ase.poker.service;

import com.sap.ase.poker.model.GameState;
import com.sap.ase.poker.model.IllegalActionException;
import com.sap.ase.poker.model.IllegalAmountException;
import com.sap.ase.poker.model.Player;
import com.sap.ase.poker.model.deck.Card;
import com.sap.ase.poker.model.deck.Deck;
import com.sap.ase.poker.model.deck.Kind;
import com.sap.ase.poker.model.deck.Suit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class TableService {

    private final Supplier<Deck> deckSupplier;

    private GameState state = GameState.OPEN;

    private int currentPlayerIndex = 0;

    private Map<String, List<Card>> cardsMap = new HashMap<>();

    private List<Card> communityCardsList = new ArrayList<>();

    public List<Player> players = new ArrayList<>();

    public Player winner;

    int potAmount = 0;

    Map<String, Integer> betMap = new HashMap<String, Integer>();

    public TableService(Supplier<Deck> deckSupplier) {
        this.deckSupplier = deckSupplier;
    }

    public GameState getState() {
        return state;
    }


    public List<Player> getPlayers() {
        return players;
    }

    public List<Card> getPlayerCards(String playerId) {
        return null == cardsMap.get(playerId) ? new ArrayList<>() : cardsMap.get(playerId);
    }

    public List<Card> getCommunityCards() {
        return communityCardsList;
    }

    public Optional<Player> getCurrentPlayer() {
        return state == GameState.OPEN ? Optional.empty() : Optional.of(players.get(currentPlayerIndex));
    }

    public Map<String, Integer> getBets() {
        return betMap;
    }

    public int getPot() {
        return potAmount;
    }

    public Optional<Player> getWinner() {
        return state != GameState.ENDED ? Optional.empty() : Optional.of(winner);
    }

    public List<Card> getWinnerHand() {
        // TODO: implement me
        return Arrays.asList(
                new Card(Kind.ACE, Suit.CLUBS),
                new Card(Kind.KING, Suit.CLUBS),
                new Card(Kind.QUEEN, Suit.CLUBS),
                new Card(Kind.JACK, Suit.CLUBS),
                new Card(Kind.TEN, Suit.CLUBS)
        );
    }

    public void start() {
        if (players.size() < 2) {
            throw new IllegalActionException("game can only be start with more than 2 people");
        }
        state = GameState.PRE_FLOP;
        Deck deck = deckSupplier.get();
        dealCards(deck, players);
    }

    private void dealCards(Deck deck, List<Player> players) {
        for (Player player : players) {
            List<Card> cards = new ArrayList<>();
            cards.add(deck.draw());
            cards.add(deck.draw());
            cardsMap.put(player.getId(), cards);
            player.setActive();
        }
    }

    public void addPlayer(String playerId, String playerName) {
        players.add(new Player(playerId, playerName, 100));
    }

    public void performAction(String action, int amount) throws IllegalAmountException, IllegalActionException {
        switch (action) {
            case "check":
                if (getMaxBet()>0) {
                    throw new IllegalActionException("Invalid case");
                }
                updateCurrentPlayer();
                break;
            case "fold":
                Player player = getPlayers().get(currentPlayerIndex);
                player.setInactive();
                List<Player> activePlayers = getActivePlayers();
                if (activePlayers.size() == 1) {
                    state = GameState.ENDED;
                    winner = activePlayers.get(0);
                }
                updateCurrentPlayer();
                break;
            case "raise":
                Player player1 = getPlayers().get(currentPlayerIndex);
                if (amount > player1.getCash() || amount > getPlayersCash()) {
                    throw new IllegalAmountException("Invalid Amt");
                }
                player1.bet(amount);
                betMap.put(player1.getId(), amount);
                potAmount += amount;
                updateCurrentPlayer();
                break;
            case "call":
                Player player2 = getPlayers().get(currentPlayerIndex);
                int maxBetAmount = getMaxBet();
                if (maxBetAmount > player2.getCash()) {
                    throw new IllegalAmountException("Invalid Amt");
                }

                player2.bet(maxBetAmount);
                betMap.put(player2.getId(), maxBetAmount);
                potAmount += maxBetAmount;
                updateCurrentPlayer();
                break;
            default:
                throw new IllegalActionException("Invalid action :" + action);

        }

    }

    private void updateCurrentPlayer() {
        if (currentPlayerIndex == players.size() - 1) {
            updateStateAndAddCommunityCards();
        } else {
            currentPlayerIndex++;
        }
    }

    int getMaxBet() {
        int maxBet = 0;
            if(betMap.size() != 0) {
                maxBet = Collections.max(betMap.values());
            }
        return maxBet;
    }

    private List<Player> getActivePlayers() {
        return players.stream().filter(Player::isActive).collect(Collectors.toList());
    }

    private int getPlayersCash() {
        int minAmt = 100;
        for (Player player : players) {
            if (player.getCash() < minAmt) {
                minAmt = player.getCash();
            }
        }
        return minAmt;  //20 30 10
    }

    private void updateStateAndAddCommunityCards() {
        currentPlayerIndex = 0;
        if (state == GameState.PRE_FLOP) {
            state = GameState.FLOP;
            communityCardsList.add(deckSupplier.get().draw());
            communityCardsList.add(deckSupplier.get().draw());
            communityCardsList.add(deckSupplier.get().draw());
        } else if (state == GameState.FLOP) {
            state = GameState.TURN;
            communityCardsList.add(deckSupplier.get().draw());
        } else if (state == GameState.TURN) {
            state = GameState.RIVER;
            communityCardsList.add(deckSupplier.get().draw());
        }
    }

}
