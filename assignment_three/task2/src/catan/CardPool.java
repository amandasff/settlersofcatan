package catan;

import java.util.Random;

public final class CardPool {
    private static int knightsRemaining = 14;
    private static int roadBuildingRemaining = 2;
    private static int yearOfPlentyRemaining = 2;
    private static int monopolyRemaining = 2;
    private static int vpCardRemaining = 5;

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
        return vpCardRemaining > 0;
    }

    private static int totalCardsRemaining() {
        return knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining + monopolyRemaining + vpCardRemaining;
    }

    private static CardType drawKnight() {
        knightsRemaining--;
        return CardType.KNIGHT;
    }

    private static CardType drawRoadBuilding() {
        roadBuildingRemaining--;
        return CardType.ROADBUILDING;
    }

    private static CardType drawYearOfPlenty() {
        yearOfPlentyRemaining--;
        return CardType.YEAROFPLENTY;
    }

    private static CardType drawMonopoly() {
        monopolyRemaining--;
        return CardType.MONOPOLY;
    }

    private static CardType drawVPCard() {
        vpCardRemaining--;
        return CardType.VPCARD;
    }

    public static CardType drawCard(Player player) {
        if (totalCardsRemaining() == 0) {
            return null;
        }

        Random random = new Random();
        int nextCard = random.nextInt(totalCardsRemaining());
        CardType result;

        if (nextCard < knightsRemaining) {
            result = drawKnight();
        } else if (nextCard < knightsRemaining + roadBuildingRemaining) {
            result = drawRoadBuilding();
        } else if (nextCard < knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining) {
            result = drawYearOfPlenty();
        } else if (nextCard < knightsRemaining + roadBuildingRemaining + yearOfPlentyRemaining + monopolyRemaining) {
            result = drawMonopoly();
        } else {
            result = drawVPCard();
            player.addVictoryPoints(1);
            return result;
        }

        player.getDevelopmentHand().put(result, player.getDevelopmentHand().get(result) + 1);
        return result;
    }
}
