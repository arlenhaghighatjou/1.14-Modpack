package net.minecraft.util;

public class ActionResult<T> {
    private final ActionResultType type;
    private final T result;

    public ActionResult(ActionResultType typeIn, T resultIn) {
        this.type = typeIn;
        this.result = resultIn;
    }

    public ActionResultType getType() {
        return this.type;
    }

    public T getResult() {
        return this.result;
    }
}
