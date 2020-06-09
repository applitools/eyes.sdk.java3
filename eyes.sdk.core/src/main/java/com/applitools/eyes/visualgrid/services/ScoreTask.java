package com.applitools.eyes.visualgrid.services;

public class ScoreTask {

    private VisualGridTask task;
    private int score;

    ScoreTask(VisualGridTask task, int score) {
        this.task = task;
        this.score = score;
    }

    public int getScore() {
        return score;
    }

    VisualGridTask getVisualGridTask(){
        task.setIsSent();
        return task;
    }
}
