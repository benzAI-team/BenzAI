package generator.patterns;

public enum PatternLabel { VOID, POSITIVE, NEGATIVE, NEUTRAL;
    public static PatternLabel next(PatternLabel label){
        return values()[(label.ordinal() + 1) % values().length];
    }
}
