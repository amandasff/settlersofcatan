package catan;

public class Road {
    private final Player owner;
    private final Edge location;

    public Road(Player owner, Edge location) {
        this.owner = owner;
        this.location = location;
    }

    public Player getOwner() { return owner; }
    public Edge getLocation() { return location; }
}
