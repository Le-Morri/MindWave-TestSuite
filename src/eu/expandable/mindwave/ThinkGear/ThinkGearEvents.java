package eu.expandable.mindwave.ThinkGear;

/**
 *
 * @author Andre
 */
public interface ThinkGearEvents {
    public void onBlink(int strength);
    
    public void onScan(int poorSignalLevel, String status);
    
    public void onMentalEffort(float effort);
    
    public void onFamiliarity(float familiarity);
    
    public void onNewThought(int attention, int meditation, int delta, int theta, int lowAlpha, int highAlpha, int lowBeta, int highBeta, int lowGamma, int highGamma, int poorSignalLevel);
}
