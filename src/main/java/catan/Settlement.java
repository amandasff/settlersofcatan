package catan;

public class Settlement extends Building {
    public Settlement(Player owner, Node location) {
        super(owner, location);
    }

    @Override
    public int getVPValue() { return 1; }
}
