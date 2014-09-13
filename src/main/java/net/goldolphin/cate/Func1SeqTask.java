package net.goldolphin.cate;

/**
 * @author goldolphin
 *         2014-09-06 16:48
 */
public class Func1SeqTask<T, AResult, TResult> extends SeqTask<AResult, TResult> {
    private final Func1<T, TResult> func;

    public Func1SeqTask(ITask<AResult> antecedent, Func1<T, TResult> func, boolean flatten) {
        super(antecedent, flatten);
        this.func = func;
    }

    @Override
    public TResult evaluate(Object value) {
        return func.apply((T) value);
    }
}
