package catan;

import java.util.List;

public interface AgentStrategy {
    Action select(List<Action> options);
}
