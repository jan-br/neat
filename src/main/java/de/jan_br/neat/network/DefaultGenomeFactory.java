package de.jan_br.neat.network;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import de.jan_br.neat.NeatAlgorithm;
import de.jan_br.neat.NeatAlgorithmConfiguration;
import de.jan_br.neat.util.RandomUtils;

@Singleton
public final class DefaultGenomeFactory implements Provider<Genome> {

  private final NeatAlgorithm neatAlgorithm;
  private final InnovationCounter innovationCounter;
  private final NeatAlgorithmConfiguration neatAlgorithmConfiguration;

  @Inject
  private DefaultGenomeFactory(
      NeatAlgorithm neatAlgorithm,
      InnovationCounter innovationCounter,
      NeatAlgorithmConfiguration neatAlgorithmConfiguration) {
    this.neatAlgorithm = neatAlgorithm;
    this.innovationCounter = innovationCounter;
    this.neatAlgorithmConfiguration = neatAlgorithmConfiguration;
  }

  public Genome get() {
    Integer[] inputs = new Integer[this.neatAlgorithmConfiguration.getInputs()];
    for (int i = 0; i < inputs.length; i++) inputs[i] = i + 1;

    Integer[] outputs = new Integer[this.neatAlgorithmConfiguration.getOutputs()];
    for (int i = 0; i < outputs.length; i++) outputs[i] = inputs.length + i + 1;

    float dist = this.neatAlgorithmConfiguration.getMutationWeightChanceRandomRange();
    Genome gen =
        new Genome(null, inputs, outputs, this.neatAlgorithmConfiguration, this.neatAlgorithm);
    for (int in = 1; in <= this.neatAlgorithmConfiguration.getInputs(); in++) {
      for (int out = 1; out <= this.neatAlgorithmConfiguration.getOutputs(); out++) {
        gen.addGene(
            new Gene(
                this.innovationCounter.get(),
                in,
                this.neatAlgorithmConfiguration.getInputs() + out,
                RandomUtils.randomValue(-dist, dist),
                true),
            null,
            null);
      }
    }
    return gen;
  }
}
