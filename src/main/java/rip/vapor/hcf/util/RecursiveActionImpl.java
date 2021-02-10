package rip.vapor.hcf.util;

import lombok.RequiredArgsConstructor;

import java.util.concurrent.RecursiveAction;
import java.util.function.Consumer;

@RequiredArgsConstructor
public class RecursiveActionImpl<T> extends RecursiveAction {

    private final Consumer<T> action;
    private final T object;

    @Override
    protected void compute() {
        this.action.accept(object);
    }
}