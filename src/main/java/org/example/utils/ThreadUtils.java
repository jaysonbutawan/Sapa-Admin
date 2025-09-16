package org.example.utils;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;
import java.util.function.Supplier;


public class ThreadUtils {
    private static final ExecutorService executorService = Executors.newCachedThreadPool(r -> {
        Thread thread = new Thread(r);
        thread.setDaemon(true);
        thread.setName("Background-Worker-" + thread.getId());
        return thread;
    });

    public static <T> void executeInBackground(
            Supplier<T> task,
            Consumer<T> onSuccess,
            Consumer<Exception> onError) {

        CompletableFuture.supplyAsync(task, executorService)
            .whenComplete((result, throwable) -> {
                if (throwable != null) {
                    if (onError != null) {
                        onError.accept(throwable instanceof Exception ?
                            (Exception) throwable :
                            new Exception(throwable));
                    }
                } else {
                    if (onSuccess != null) {
                        onSuccess.accept(result);
                    }
                }
            });
    }

}
