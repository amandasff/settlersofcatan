package catan;

public class Building {
    private final Player owner;
    private final Node location;

    public Building(Player owner, Node location) {
        this.owner = owner;
        this.location = location;
    }

    public Player getOwner() { return owner; }
    public Node getLocation() { return location; }

    public int getVPValue() { return 0; }
}
