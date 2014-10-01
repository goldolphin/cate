package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-06 16:48
 */
public class Action1SeqTask<T, AResult> extends SeqTask<AResult, Unit> {
    private final Action1<T> action;

    public Action1SeqTask(ITask<AResult> antecedent, Action1<T> action, boolean flatten) {
        super(antecedent, flatten);
        this.action = action;
    }

    @Override
    public Unit evaluate(Object value) {
        action.apply((T) value);
        return Unit.INSTANCE;
    }
}
