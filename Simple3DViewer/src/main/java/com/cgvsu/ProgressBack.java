package com.cgvsu;

import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

public class ProgressBack {

    public final ProgressBar progressBar;

    public ProgressBack(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void doSomething(double progress) throws InterruptedException {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                progressBar.setProgress(progress);
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
        t.join();
    }
}
