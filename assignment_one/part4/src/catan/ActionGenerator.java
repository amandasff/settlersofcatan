package catan;

import java.util.ArrayList;
import java.util.List;

public final class ActionGenerator {

    public List<Action> getExecutableActions(GameState state, Player player, boolean mustBuild) {
        List<Action> buildActions = new ArrayList<>();

        for (Edge edge : state.getBoard().getEdges()) {
            BuildRoadAction action = new BuildRoadAction(edge);
            if (action.isExecutable(state, player)) {
                buildActions.add(action);
            }
        }

        for (Node node : state.getBoard().getNodes()) {
            BuildSettlementAction settlementAction = new BuildSettlementAction(node);
            if (settlementAction.isExecutable(state, player)) {
                buildActions.add(settlementAction);
            }

            UpgradeToCityAction cityAction = new UpgradeToCityAction(node);
            if (cityAction.isExecutable(state, player)) {
                buildActions.add(cityAction);
            }
        }

        if (mustBuild) {
            if (!buildActions.isEmpty()) {
                return buildActions;
            }
            return List.of(new PassAction());
        }

        List<Action> allActions = new ArrayList<>(buildActions);
        allActions.add(new PassAction());
        return allActions;
    }
}