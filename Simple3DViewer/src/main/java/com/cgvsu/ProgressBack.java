package com.cgvsu;

import com.cgvsu.objreader.ObjReader;
import javafx.concurrent.Task;
import javafx.scene.control.ProgressBar;

public class ProgressBack {

    public final ProgressBar progressBar;

    public ProgressBack(ProgressBar progressBar) {
        this.progressBar = progressBar;
    }

    public void doSomething() throws InterruptedException {
        Task<Void> task = new Task<>() {
            @Override
            public Void call() {
                progressBar.setProgress(ObjReader.n);
                return null;
            }
        };
        Thread t = new Thread(task);
        t.start();
        t.join();
    }
}
