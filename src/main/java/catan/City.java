package catan;

public class City extends Building {
    public City(Player owner, Node location) {
        super(owner, location);
    }

    @Override
    public int getVPValue() { return 2; }
}
