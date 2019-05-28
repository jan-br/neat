package de.jan_brh.neat;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import de.jan_brh.neat.network.Gene;
import de.jan_brh.neat.network.GeneType;
import de.jan_brh.neat.network.Genome;
import de.jan_brh.neat.network.GenomePrinter;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Function;

public final class NeatAlgorithm {

  private final Collection<Genome> genomes = new CopyOnWriteArrayList<>();
  private final Function<NeatInputAdapter, Float> neatTask;
  private final Runnable clearAction;
  private final Runnable initAction;
  private final Function<Float, Float> activationFunction;
  private final int parallelRuns;
  private final int inputs;
  private final int outputs;
  private boolean running;

  protected NeatAlgorithm(
      Function<NeatInputAdapter, Float> neatTask,
      Runnable clearAction,
      Runnable initAction,
      Function<Float, Float> activationFunction,
      int parallelRuns,
      int inputs,
      int outputs) {
    this.neatTask = neatTask;
    this.clearAction = clearAction;
    this.initAction = initAction;
    this.activationFunction = activationFunction;
    this.parallelRuns = parallelRuns;
    this.inputs = inputs;
    this.outputs = outputs;
  }

  public void start() {
    if (this.running) throw new IllegalStateException("Algorithm is already active.");
    this.running = true;
    ListeningExecutorService listeningExecutorService =
        MoreExecutors.listeningDecorator(Executors.newCachedThreadPool());
    listeningExecutorService.execute(
        () -> {
          this.initAction.run();
          for (int i = 0; i < this.parallelRuns; i++) {
            Genome genome = new Genome(this.activationFunction);
            for (int j = 0; j < this.inputs; j++) {
              genome.addGene(GeneType.INPUT);
            }
            for (int j = 0; j < this.outputs; j++) {
              genome.addGene(GeneType.OUTPUT);
            }
            this.genomes.add(genome);
          }
          for (Genome genome : genomes) {
            for (Gene input : genome.getInputs()) {
              for (Gene output : genome.getOutputs()) {
                genome.addConnection(input, output, 1);
              }
            }
          }
          int counter = 0;
          for (Genome genome : this.genomes) {
            GenomePrinter.printGenome(genome, counter++ + "-genome.png");
          }

          while (this.running) {
            CompletionService<Float> completionService =
                new ExecutorCompletionService<>(listeningExecutorService);
            for (Genome genome : this.genomes)
              completionService.submit(() -> this.neatTask.apply(genome::process));

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
