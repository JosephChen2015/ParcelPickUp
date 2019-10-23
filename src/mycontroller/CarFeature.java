package mycontroller;

public abstract class CarFeature {
    public int maximumForwardSpeed;
    public int maximumBackwardSpeed;
    public int maximumAccelerations;
    public int maximumTurnings;

    public CarFeature(int maximumForwardSpeed, int maximumBackwardSpeed, int maximumAccelerations, int maximumTurnings) {
        this.maximumForwardSpeed = maximumForwardSpeed;
        this.maximumBackwardSpeed = maximumBackwardSpeed;
        this.maximumAccelerations = maximumAccelerations;
        this.maximumTurnings = maximumTurnings;
    }
}
