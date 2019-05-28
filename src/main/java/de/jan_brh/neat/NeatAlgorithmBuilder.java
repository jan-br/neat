package de.jan_brh.neat;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

public class NeatAlgorithmBuilder {

  private Supplier<Float> neatTask;
  private Runnable initAction;
  private Runnable clearAction;
  private int parallelRuns = 10;

  private NeatAlgorithmBuilder() {}

  public NeatAlgorithmBuilder setNeatTask(Supplier<Float> neatTask) {
    this.neatTask = neatTask;
    return this;
  }

  public NeatAlgorithmBuilder setParallelRuns(int parallelRuns) {
    this.parallelRuns = parallelRuns;
    return this;
  }

  public NeatAlgorithmBuilder setClearAction(Runnable clearAction) {
    this.clearAction = clearAction;
    return this;
  }

  public NeatAlgorithmBuilder setInitAction(Runnable initAction) {
    this.initAction = initAction;
    return this;
  }

  public Runnable getClearAction() {
    return clearAction;
  }

  public Runnable getInitAction() {
    return initAction;
  }

  public Supplier<Float> getNeatTask() {
    return neatTask;
  }

  public int getParallelRuns() {
    return parallelRuns;
  }

  public NeatAlgorithm build() {
    Preconditions.checkNotNull(this.neatTask);
    Preconditions.checkNotNull(this.clearAction);
    Preconditions.checkNotNull(this.initAction);
    Preconditions.checkArgument(parallelRuns >= 1);
    return new NeatAlgorithm(this.neatTask, this.clearAction, this.initAction, this.parallelRuns);
  }

  public static NeatAlgorithmBuilder newBuilder() {
    return new NeatAlgorithmBuilder();
  }
}
