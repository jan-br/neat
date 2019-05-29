package de.jan_brh.neat.network;

import de.jan_brh.neat.NeatAlgorithm;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Genome implements Cloneable {

  private final float PROBABILITY_PERTURBING = 0.09f;
  private final NeatAlgorithm neatAlgorithm;
  private final Random random = new Random();
  private final Collection<Gene> genes = new CopyOnWriteArrayList<>();
  private final Collection<GeneConnection> geneConnections = new CopyOnWriteArrayList<>();
  private final IncrementalCounter geneCounter = new IncrementalCounter();
  private final IncrementalCounter geneConnectionCounter;
  private final Function<Float, Float> activationFunction;
  private Species species;
  private float latestScore;

  public Genome(
      NeatAlgorithm neatAlgorithm,
      IncrementalCounter geneConnectionCounter,
      Function<Float, Float> activationFunction) {
    this.neatAlgorithm = neatAlgorithm;
    this.geneConnectionCounter = geneConnectionCounter;
    this.activationFunction = activationFunction;

    System.out.println("Create genome...");
  }

  /**
   * calculated the distance between this genome g1 and a second genome g2 - g1 must have the
   * highest innovation number!
   *
   * @param g2
   * @return
   */
  public double distance(Genome g2) {

    Genome g1 = this;

    int highest_innovation_gene1 =
        g1.getConnections().get(g1.getConnections().size() - 1).getInnovation();
    int highest_innovation_gene2 =
        g2.getConnections().get(g2.getConnections().size() - 1).getInnovation();

    if (highest_innovation_gene1 < highest_innovation_gene2) {
      Genome g = g1;
      g1 = g2;
      g2 = g;
    }

    int index_g1 = 0;
    int index_g2 = 0;

    int disjoint = 0;
    int excess = 0;
    double weight_diff = 0;
    int similar = 0;

    while (index_g1 < g1.getConnections().size() && index_g2 < g2.getConnections().size()) {

      GeneConnection gene1 = g1.getConnections().get(index_g1);
      GeneConnection gene2 = g2.getConnections().get(index_g2);

      int in1 = gene1.getInnovation();
      int in2 = gene2.getInnovation();

      if (in1 == in2) {
        // similargene
        similar++;
        weight_diff += Math.abs(gene1.getWeight() - gene2.getWeight());
        index_g1++;
        index_g2++;
      }
      if (in1 > in2) {
        // disjoint gene of b
        disjoint++;
        index_g2++;
      } else {
        // disjoint gene of a
        disjoint++;
        index_g1++;
      }
    }

    weight_diff /= similar;
    excess = g1.getConnections().size() - index_g1;

    double N = Math.max(g1.getConnections().size(), g2.getConnections().size());
    if (N < 20) {
      N = 1;
    }

    return neatAlgorithm.getC1() * disjoint / N
        + neatAlgorithm.getC2() * excess / N
        + neatAlgorithm.getC3() * weight_diff / N;
  }

  public Genome addGene(GeneType type) {
    Gene gene = new Gene(type, this.geneCounter.get(), this);
    System.out.println("Added Gene " + gene.getInnovation() + " with type " + type);
    return this.addGene(gene);
  }

  public Genome addGene(Gene gene) {
    this.genes.add(gene);
    return this;
  }

  public Genome addConnection(Gene from, Gene to, float weight) {
    System.out.println(
        "Added Connection from "
            + from.getInnovation()
            + " to "
            + to.getInnovation()
            + " with "
            + weight);

    Optional<GeneConnection> any =
        this.neatAlgorithm.getGenomes().stream()
            .flatMap(genome -> genome.getConnections().stream())
            .filter(
                geneConnection ->
                    geneConnection.getFrom().getInnovation() == from.getInnovation()
                        && geneConnection.getTo().getInnovation() == to.getInnovation())
            .findAny();

    int id =
        any.map(GeneConnection::getInnovation)
            .orElseGet(() -> neatAlgorithm.getGeneConnectionCounter().get());
    return this.addConnection(new GeneConnection(id, from, to, weight));
  }

  public Genome addConnection(GeneConnection geneConnection) {
    this.geneConnections.add(geneConnection);
    return this;
  }

  public List<GeneConnection> getConnectionToFrontLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getTo().equals(gene))
        .sorted(Comparator.comparingInt(connection -> connection.getFrom().getInnovation()))
        .collect(Collectors.toList());
  }

  public List<GeneConnection> getConnectionsToNextLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getFrom().equals(gene))
        .sorted(Comparator.comparingInt(connection -> connection.getTo().getInnovation()))
        .collect(Collectors.toList());
  }

  public Collection<Gene> getGenes() {
    return genes;
  }

  public Gene getGene(int innovation) {
    return this.getGenes().stream()
        .filter(gene -> gene.getInnovation() == innovation)
        .findAny()
        .orElse(null);
  }

  public List<Gene> getInputs() {
    return this.genes.stream()
        .filter(gene -> gene.getGeneType() == GeneType.INPUT)
        .sorted(Comparator.comparingInt(Gene::getInnovation))
        .collect(Collectors.toList());
  }

  public List<Gene> getOutputs() {
    return this.genes.stream()
        .filter(gene -> gene.getGeneType() == GeneType.OUTPUT)
        .sorted(Comparator.comparingInt(Gene::getInnovation))
        .collect(Collectors.toList());
  }

  public List<GeneConnection> getActiveConnections() {
    return this.geneConnections.stream()
        .filter(GeneConnection::isEnabled)
        .sorted(Comparator.comparingInt(GeneConnection::getInnovation))
        .collect(Collectors.toList());
  }

  public List<GeneConnection> getConnections() {
    return new LinkedList<>(this.geneConnections);
  }

  public Species getSpecies() {
    return species;
  }

  public void setSpecies(Species species) {
    this.species = species;
  }

  public synchronized float[] process(float[] input) {
    Collection<Gene> inputs = this.getInputs();
    if (input.length != inputs.size()) {
      new IllegalArgumentException("Input array must have a length of " + inputs.size())
          .printStackTrace();
      System.exit(1);
    }
    int counter = 0;
    for (Gene gene : inputs) {
      gene.input(input[counter++]);
    }
    Collection<Gene> outputs = getOutputs();
    float[] result = new float[outputs.size()];
    counter = 0;
    for (Gene output : outputs) {
      result[counter++] = output.getOutput();
    }
    return result;
  }

  public void mutation() {
    for (GeneConnection con : this.geneConnections) {
      if (this.random.nextFloat() < PROBABILITY_PERTURBING) { // uniformly perturbing weights
        con.setWeight(con.getWeight() * (this.random.nextFloat() * 4f - 2f));
      } else { // assigning new weight
        con.setWeight(this.random.nextFloat() * 4f - 2f);
      }
    }
  }

  public void addNodeMutation() {
    GeneConnection con = this.geneConnections.stream().findAny().orElse(null);

    Gene inNode = con.getFrom();
    Gene outNode = con.getTo();

    con.setEnabled(false);

    this.genes.stream()
        .forEach(
            nodeGene ->
                getConnectionsToNextLayer(nodeGene)
                    .forEach(
                        iNodeConnection -> {
                          if (!iNodeConnection.isEnabled()) {
                            geneConnections.remove(iNodeConnection);
                          }
                        }));

    Gene newNode = new Gene(GeneType.HIDDEN, neatAlgorithm.getGeneConnectionCounter().get(), this);
    GeneConnection inToNew =
        new GeneConnection(
            this.geneConnectionCounter.get(),
            inNode,
            newNode,
            this.random.nextFloat() * (this.random.nextBoolean() ? -1 : 1),
            true);
    GeneConnection newToOut =
        new GeneConnection(
            this.geneConnectionCounter.get(), newNode, outNode, con.getWeight(), true);

    this.addGene(newNode);
    this.addConnection(inToNew);
    this.addConnection(newToOut);
  }

  public void addConnectionMutation(int maxAttempts) {
    int tries = 0;
    boolean success = false;
    while (tries < maxAttempts && success == false) {
      tries++;

      List<Gene> geneList = new LinkedList<>(this.genes);
      Gene node1 = geneList.get(this.random.nextInt(geneList.size() - 1));
      Gene node2 = geneList.get(this.random.nextInt(geneList.size() - 1));
      float weight = this.random.nextFloat() * (this.random.nextBoolean() ? -1 : 1);

      boolean reversed = false;
      if (node1.getGeneType() == GeneType.HIDDEN && node2.getGeneType() == GeneType.INPUT) {
        reversed = true;
      } else if (node1.getGeneType() == GeneType.OUTPUT && node2.getGeneType() == GeneType.HIDDEN) {
        reversed = true;
      } else if (node1.getGeneType() == GeneType.OUTPUT && node2.getGeneType() == GeneType.INPUT) {
        reversed = true;
      }

      boolean connectionImpossible = false;
      if (node1.getGeneType() == GeneType.INPUT && node2.getGeneType() == GeneType.INPUT) {
        connectionImpossible = true;
      } else if (node1.getGeneType() == GeneType.OUTPUT && node2.getGeneType() == GeneType.OUTPUT) {
        connectionImpossible = true;
      }

      boolean connectionExists = false;
      for (GeneConnection con : this.geneConnections) {
        if (con.getFrom().getInnovation() == node1.getInnovation()
            && con.getTo().getInnovation() == node2.getInnovation()) { // existing connection
          connectionExists = true;
          break;
        } else if (con.getFrom().getInnovation() == node2.getInnovation()
            && con.getTo().getInnovation() == node1.getInnovation()) { // existing connection
          connectionExists = true;
          break;
        }
      }

      if (connectionExists || connectionImpossible) {
        continue;
      }

      if (reversed) {
        this.addConnection(node2, node1, weight);
      } else {
        this.addConnection(node1, node2, weight);
      }

      success = true;
    }
  }

  public Function<Float, Float> getActivationFunction() {
    return activationFunction;
  }

  public void setLatestScore(float latestScore) {
    this.latestScore = latestScore;
  }

  public float getLatestScore() {
    return this.latestScore;
  }

  public Genome clone() {
    Genome genome = new Genome(neatAlgorithm, geneConnectionCounter, activationFunction);
    for (Gene gene : this.getGenes()) {
      genome.addGene(gene.clone(genome));
    }
    for (GeneConnection connection : this.getConnections()) {
      genome.addConnection(
          new GeneConnection(
              connection.getInnovation(),
              genome.getGene(connection.getFrom().getInnovation()),
              genome.getGene(connection.getTo().getInnovation()),
              connection.getWeight(),
              connection.isEnabled()));
    }
    return genome;
  }
}
