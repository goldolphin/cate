package net.goldolphin.cate;

/**
 * A basic continuation for combining several continuations in sequence.
 * @author goldolphin
 *         2014-10-18 22:01
 */
public class SeqContinuation implements IContinuation {
    private final IContinuation antecedent;
    private final IContinuation subsequent;

    private SeqContinuation(IContinuation antecedent, IContinuation subsequent) {
        this.antecedent = antecedent;
        this.subsequent = subsequent;
    }

    @Override
    public void apply(Object state, IContinuation subCont, IScheduler scheduler) {
        antecedent.apply(state, seq(subsequent, subCont), scheduler);
    }

    /**
     * Combine 2 continuations in sequence.
     * @param antecedent
     * @param subsequent
     * @return
     */
    public static IContinuation seq(IContinuation antecedent, IContinuation subsequent) {
        if (subsequent == END_CONTINUATION) {
            return antecedent;
        }
        return new SeqContinuation(antecedent, subsequent);
    }
}
