package de.jan_brh.neat;

import com.google.common.base.Supplier;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;

import java.util.Collection;
import java.util.concurrent.*;

public final class NeatAlgorithm {

  private final Supplier<Float> neatTask;
  private final Runnable clearAction;
  private final Runnable initAction;
  private final int parallelRuns;
  private boolean running;

  protected NeatAlgorithm(
      Supplier<Float> neatTask, Runnable clearAction, Runnable initAction, int parallelRuns) {
    this.neatTask = neatTask;
    this.clearAction = clearAction;
    this.initAction = initAction;
    this.parallelRuns = parallelRuns;
  }

  public void start() {
    if (this.running) throw new IllegalStateException("Algorithm is already active.");
    this.running = true;
    ListeningExecutorService listeningExecutorService =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    listeningExecutorService.execute(
        () -> {
          this.initAction.run();
          while (this.running) {
            CompletionService<Float> completionService =
                new ExecutorCompletionService<>(listeningExecutorService);
            for (int i = 0; i < this.parallelRuns; i++) {
              completionService.submit(neatTask::get);
            }

            for (int i = 0; i < this.parallelRuns; i++) {
              try {
                completionService.take();
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
            }
            System.out.println("Finished...");
            return;
          }
        });
  }

  public void stop() {
    if (!this.running) throw new IllegalStateException("Algorithm is already inactive.");
    this.running = false;
  }
}
