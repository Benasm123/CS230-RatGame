package RatGame;

public class NoEntrySign extends Item {
    @Override
    public void use() {
        System.out.println("Stop sign used?");
    }

    @Override
    public void steppedOn() {

    }
}
