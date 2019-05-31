package de.jan_br.neat.network;

import de.jan_br.neat.util.RandomUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;

public enum MutationType {
  ADD_NODE(
      genome -> {
        Gene randomGene = RandomUtils.randomItem(new ArrayList<>(genome.getGenes()));
        randomGene.setEnabled(false);

        int from = randomGene.getFrom();
        int to = randomGene.getTo();

        genome.getNeatAlgorithm().getInnovationCounter().get();

        int newNodeId = genome.getHighestNode() + 1;
        genome.addGene(
            new Gene(
                genome.getNeatAlgorithm().getInnovationCounter().get(), from, newNodeId, 1f, true),
            null,
            null);
        genome.addGene(
            new Gene(
                genome.getNeatAlgorithm().getInnovationCounter().get(),
                newNodeId,
                to,
                randomGene.getWeight(),
                true),
            null,
            null);
      }),
  ADD_CONNECTION(
      genome -> {
        Collection<? extends GeneConnection> currentConnections = genome.getAllConnections();

        int attempts = 0;

        GeneConnection maybeNew = null;
        do {
          if (attempts++ > 40) return;

          int from = RandomUtils.randomItem(genome.getNodes(true, true, false));

          List<Integer> leftOver = genome.getNodes(false, true, true);
          leftOver.remove((Integer) from);
          if (leftOver.isEmpty()) continue;

          int to = RandomUtils.randomItem(leftOver);

          maybeNew = new GeneConnection(from, to);
        } while (maybeNew == null
            || maybeNew.getFrom() == maybeNew.getTo()
            || currentConnections.contains(maybeNew)
            || isRecurrent(genome, maybeNew));

        genome.addGene(
            new Gene(
                genome.getNeatAlgorithm().getInnovationCounter().get(),
                maybeNew.getFrom(),
                maybeNew.getTo(),
                RandomUtils.randomValue(-1, 1),
                true),
            null,
            null);
      }),
  MODIFY_WEIGHT(
      genome -> {
        if (RandomUtils.success(
            genome.getNeatAlgorithmConfiguration().getMutationWeightRandomChance())) {
          for (Gene gene : genome.getGenes()) {
            float range =
                genome.getNeatAlgorithmConfiguration().getMutationWeightChanceRandomRange();
            gene.setWeight(RandomUtils.randomValue(-range, range));
          }
        } else {
          for (Gene gene : genome.getGenes()) {
            float disturbance =
                genome.getNeatAlgorithmConfiguration().getMutationWeightMaxDisturbance();
            float uniform = RandomUtils.randomValue(-disturbance, disturbance);
            gene.setWeight(gene.getWeight() + uniform);
          }
        }
      });

  private final Consumer<Genome> consumer;

  MutationType(Consumer<Genome> consumer) {
    this.consumer = consumer;
  }

  public static void mutate(Genome genome) {

    if (RandomUtils.success(genome.getNeatAlgorithmConfiguration().getMutationNewNodeChance())) {
      ADD_NODE.consumer.accept(genome);
    }

    if (RandomUtils.success(
        genome.getNeatAlgorithmConfiguration().getMutationNewConnectionChance())) {
      ADD_CONNECTION.consumer.accept(genome);
    }

    if (RandomUtils.success(genome.getNeatAlgorithmConfiguration().getMutationWeightChance())) {
      MODIFY_WEIGHT.consumer.accept(genome);
    }
  }

  private static boolean isRecurrent(Genome genome, GeneConnection with) {
    Genome tmpGenome = genome.clone();
    if (with != null) {
      Gene gene =
          new Gene(
              tmpGenome.getHighestInnovationNumber() + 1, with.getFrom(), with.getTo(), 0, true);
      tmpGenome.addGene(gene, null, null);
    }

    boolean recc = false;
    for (int hiddenNode : tmpGenome.getHiddenNodes()) {
      if (isRecurrent(new ArrayList<>(), tmpGenome, hiddenNode)) {
        recc = true;
      }
    }
    return recc;
  }

  private static boolean isRecurrent(List<Integer> path, Genome genome, int node) {
    if (path.contains(node)) {
      return true;
    }
    path.add(node);

    boolean recc = false;
    for (int from : getInputs(genome, node)) {
      if (!genome.isInputNode(from)) {
        if (isRecurrent(path, genome, from)) {
          recc = true;
        }
      }
    }
    return recc;
  }

  private static List<Integer> getInputs(Genome genome, int node) {
    List<Integer> froms = new ArrayList<>();
    for (Gene gene : genome.getGenes()) {
      if (gene.getTo() == node) {
        froms.add(gene.getFrom());
      }
    }
    return froms;
  }
}
