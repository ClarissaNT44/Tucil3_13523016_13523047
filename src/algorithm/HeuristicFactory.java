package algorithm;

public class HeuristicFactory { // harusnya public
    public static final int DISTANCE_HEURISTIC = 1;
    public static final int BLOCKING_VEHICLES_HEURISTIC = 2;
    public static final int COMBINED_HEURISTIC = 3;
    
    /**
     * Creates a heuristic based on the specified type.
     * 
     * @param type The type of heuristic to create
     * @return The heuristic instance
     */
    public static Heuristic createHeuristic(int type) {
        switch (type) {
            case DISTANCE_HEURISTIC:
                return new DistanceHeuristic();
            case BLOCKING_VEHICLES_HEURISTIC:
                return new BlockingVehiclesHeuristic();
            case COMBINED_HEURISTIC:
                return new CombinedHeuristic();
            default:
                return new CombinedHeuristic(); // Default to combined heuristic
        }
    }
}
