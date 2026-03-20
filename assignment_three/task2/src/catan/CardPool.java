package catan;

import java.util.Random; 

public final static class CardPool {
    private int knightsRemaining = 14;
    private int roadBuildingRemaining = 2;
    private int yearOfPlentyRemaining = 2;
    private int monopolyRemaining = 2;
    private int vpCardRemaining = 5;

    public static boolean hasKnight() {
        return knightsRemaining > 0;
    }

    public static boolean hasRoadBuilding() {
        return roadBuildingRemaining > 0;
    }

    public static boolean hasYearOfPlenty() {
        return yearOfPlentyRemaining > 0;
    }

    public static boolean hasMonopoly() {
        return monopolyRemaining > 0;
    }

    public static boolean hasVPCardRemaining() {
        return vpCardRemaining  > 0;
    }

    private static int totalCardsRemaining() {
        return (knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining + monopolyRemaining + vpCardRemaining);
    }

    private static CardType drawKnight() {
        knightsRemaining--;
        return KNIGHT;
    }

    private static CardType drawRoadBuilding() {
        roadBuildingRemaining--;
        return ROADBUILDING;
    }

    private static CardType drawYearOfPlenty() {
        yearOfPlentyRemaining--;
        return YEAROFPLENTY;
    }

    private static CardType drawMonopoly() {
        monopolyRemaining--;
        return MONOPOLY;
    }

    private static CardType drawVPCard() {
        vpCardRemaining--;
        return VPCARD;
    }

    public static CardType drawCard(Player player) {
        Random random = new Random();
        if (totalCardsRemaining() == 0) {
            return null; 
        }
        int nextCard = random.nextInt(totalCardsRemaining());
        CardType result;
        if (nextCard < knightsRemaining) {
            result = drawKnight();
        } else if (nextCard < (knightsRemaining + roadBuildingRemaining)) {
            result = drawRoadBuilding();
        } else if (nextCard < (knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining)) {
            result = drawYearOfPlenty();
        } else if (nextCard < (knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining + monopolyRemaining)) {
            result = drawMonopoly(); 
        } else {
            result = drawVPCard();
            player.addVictoryPoints(1);
            return result;
        }
        
        (player.getDevelopmentHand()).put(result, (player.getDevelopmentHand).get(result) + 1);

        return result;
    }
}