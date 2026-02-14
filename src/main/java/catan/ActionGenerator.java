package catan;

import java.util.List;
import java.util.ArrayList;

public class ActionGenerator {
    private final RuleEngine rules;

    public ActionGenerator(RuleEngine rules) {
        this.rules = rules;
    }

    public List<Action> getExecutableActions(GameState state, Player p, boolean mustBuild) {
        List<Action> actions = new ArrayList<>();
        Board board = state.getBoard();

        // Build road actions
        if (p.canAfford(Cost.roadCost())) {
            for (Edge e : board.getAllEdges()) {
                if (rules.canBuildRoad(p, e)) {
                    actions.add(new BuildRoadAction(e));
                }
            }
        }

        // Build settlement actions
        if (p.canAfford(Cost.settlementCost())) {
            for (Node n : board.getNodes()) {
                if (rules.canBuildSettlement(p, n)) {
                    actions.add(new BuildSettlementAction(n));
                }
            }
        }

        // Upgrade to city actions
        if (p.canAfford(Cost.cityCost())) {
            for (Node n : board.getNodes()) {
                if (rules.canUpgradeToCity(p, n)) {
                    actions.add(new UpgradeToCityAction(n));
                }
            }
        }

        // If mustBuild is false, or no build actions available, add pass
        if (!mustBuild || actions.isEmpty()) {
            actions.add(new PassAction());
        }

        return actions;
    }
}
