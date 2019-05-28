package de.jan_brh.neat;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;

import java.util.function.Function;

public class NeatAlgorithmBuilder {

  private Function<Float, Float> activationFunction = x -> (float) (1 / (1 + Math.exp(-x)));
  private Function<NeatInputAdapter, Float> neatTask;
  private Runnable initAction;
  private Runnable clearAction;
  private int parallelRuns = 10;
  private int inputs;
  private int outputs;

  private NeatAlgorithmBuilder() {}

  public NeatAlgorithmBuilder setNeatTask(Function<NeatInputAdapter, Float> neatTask) {
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

  public NeatAlgorithmBuilder setInputs(int inputs) {
    this.inputs = inputs;
    return this;
  }

  public NeatAlgorithmBuilder setOutputs(int outputs) {
    this.outputs = outputs;
    return this;
  }

  public NeatAlgorithmBuilder setActivationFunction(Function<Float, Float> activationFunction) {
    this.activationFunction = activationFunction;
    return this;
  }

  public Runnable getClearAction() {
    return clearAction;
  }

  public Runnable getInitAction() {
    return initAction;
  }

  public Function<NeatInputAdapter, Float> getNeatTask() {
    return neatTask;
  }

  public int getParallelRuns() {
    return parallelRuns;
  }

  public int getInputs() {
    return inputs;
  }

  public int getOutputs() {
    return outputs;
  }

  public Function<Float, Float> getActivationFunction() {
    return activationFunction;
  }

  public NeatAlgorithm build() {
    Preconditions.checkNotNull(this.neatTask);
    Preconditions.checkNotNull(this.clearAction);
    Preconditions.checkNotNull(this.initAction);
    Preconditions.checkNotNull(this.activationFunction);
    Preconditions.checkArgument(parallelRuns >= 1);
    Preconditions.checkArgument(this.inputs >= 1);
    Preconditions.checkArgument(this.outputs >= 1);
    return new NeatAlgorithm(
        this.neatTask,
        this.clearAction,
        this.initAction,
        this.activationFunction,
        this.parallelRuns,
        this.inputs,
        this.outputs);
  }

  public static NeatAlgorithmBuilder newBuilder() {
    return new NeatAlgorithmBuilder();
  }
}
