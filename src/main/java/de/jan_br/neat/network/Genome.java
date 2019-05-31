package de.jan_br.neat.network;

import com.google.common.base.Preconditions;
import de.jan_br.neat.NeatAlgorithm;
import de.jan_br.neat.NeatAlgorithmConfiguration;
import de.jan_br.neat.util.RandomUtils;

import java.util.*;

public class Genome implements Cloneable {

  private final NeatAlgorithmConfiguration neatAlgorithmConfiguration;
  private final NeatAlgorithm neatAlgorithm;
  private Map<Integer, Gene> genes = new TreeMap<>();

  private List<Integer> inputNodes = new ArrayList<>();
  private List<Integer> outputNodes = new ArrayList<>();

  private Species species;

  public Genome(
      Species member,
      Integer[] inputNodes,
      Integer[] outputNodes,
      NeatAlgorithmConfiguration neatAlgorithmConfiguration,
      NeatAlgorithm neatAlgorithm) {
    this.species = member;
    this.neatAlgorithmConfiguration = neatAlgorithmConfiguration;
    this.neatAlgorithm = neatAlgorithm;

    for (int in : inputNodes) this.addInputNode(in);

    for (int out : outputNodes) this.addOutputNode(out);
  }

  public void setSpecies(Species sp) {
    if (this.fitness != -1)
      throw new UnsupportedOperationException("setSpecies() must be called before getFitness()");
    this.species = sp;
  }

  public Species getSpecies() {
    return species;
  }

  public Integer[] getInputs() {
    return this.inputNodes.toArray(new Integer[this.inputNodes.size()]);
  }

  public Integer[] getOutputs() {
    return this.outputNodes.toArray(new Integer[this.outputNodes.size()]);
  }

  public List<Integer> getNodes(
      boolean includeInput, boolean includeHidden, boolean includeOutput) {
    List<Integer> ids = new ArrayList<>();

    for (int input : this.getAllNodes()) {
      if (this.isInputNode(input) && !includeInput) continue;
      if (this.isHiddenNode(input) && !includeHidden) continue;
      if (this.isOutputNode(input) && !includeOutput) continue;

      ids.add(input);
    }

    return ids;
  }

  public int getHighestNode() {
    List<Integer> its = this.getAllNodes();
    return its.get(its.size() - 1);
  }

  public List<Integer> getAllNodes() {
    List<Integer> ids = new ArrayList<>();
    for (Gene gene : this.getGenes()) {
      if (!ids.contains(gene.getFrom())) {
        ids.add(gene.getFrom());
      }
      if (!ids.contains(gene.getTo())) {
        ids.add(gene.getTo());
      }
    }
    Collections.sort(ids);
    return ids;
  }

  public boolean isHiddenNode(int node) {
    return !this.isInputNode(node) && !this.isOutputNode(node);
  }

  public void addInputNode(int node) {
    if (this.fitness != -1)
      throw new UnsupportedOperationException("addInputNode() must be called before getFitness()");

    if (this.inputNodes.contains(node)) throw new IllegalArgumentException();

    this.inputNodes.add(node);
  }

  public void addOutputNode(int node) {
    if (this.fitness != -1)
      throw new UnsupportedOperationException("addOutputNode() must be called before getFitness()");

    if (this.outputNodes.contains(node)) throw new IllegalArgumentException();

    this.outputNodes.add(node);
  }

  public List<Integer> getInputNodes() {
    return inputNodes;
  }

  public List<Integer> getOutputNodes() {
    return outputNodes;
  }

  public List<Integer> getHiddenNodes() {
    List<Integer> its = new ArrayList<>();
    for (int node : this.getAllNodes()) {
      if (!this.isInputNode(node) && !this.isOutputNode(node)) {
        its.add(node);
      }
    }
    return its;
  }

  public boolean isInputNode(int node) {
    return this.inputNodes.contains(node);
  }

  public boolean isOutputNode(int node) {
    return this.outputNodes.contains(node);
  }

  public void addGene(Gene gene, Genome parent1, Genome parent2) {

    if (this.fitness != -1)
      throw new UnsupportedOperationException("addGene() must be called before getFitness()");

    if (this.genes.containsKey(gene.getInnovationNumber())) {
      throw new UnsupportedOperationException(
          "Genome already has gene with innovation number " + gene.getInnovationNumber());
    }

    gene = gene.clone();
    if (parent1 != null && parent2 != null) {
      if (parent1.hasGene(gene.getInnovationNumber())
          && parent2.hasGene(gene.getInnovationNumber())) {
        boolean dis1 = !parent1.getGene(gene.getInnovationNumber()).isEnabled();
        boolean dis2 = !parent2.getGene(gene.getInnovationNumber()).isEnabled();

        if ((dis1 && !dis2) || (!dis1 && dis2)) {
          boolean disabled = RandomUtils.success(neatAlgorithmConfiguration.getGeneDisableChance());
          gene.setEnabled(!disabled);
        }
      }
    }

    this.genes.put(
        gene.getInnovationNumber(), gene);
  }

  public Collection<Gene> getGenes() {
    return genes.values();
  }

  public NeatAlgorithm getNeatAlgorithm() {
    return neatAlgorithm;
  }

  public int getHighestInnovationNumber() {
    if (this.genes.isEmpty()) {
      throw new UnsupportedOperationException("Genes may not be empty");
    }
    Iterator<Gene> it = this.genes.values().iterator();
    Gene last = null;
    while (it.hasNext()) {
      last = it.next();
    }
    if (last == null) throw new AssertionError();

    return last.getInnovationNumber();
  }

  private boolean hasGene(int innovationNumber) {
    return this.genes.containsKey(innovationNumber);
  }

  private Gene getGene(int innovationNumber) {
    return this.genes.get(innovationNumber);
  }

  public List<GeneConnection> getAllConnections() {
    List<GeneConnection> conns = new ArrayList<>();
    for (Gene gene : this.getGenes()) {
      conns.add(new GeneConnection(gene.getFrom(), gene.getTo()));
    }
    return conns;
  }

  public Collection<? extends GeneConnection> getActiveConnections() {
    Set<GeneConnection> conns = new HashSet<>();
    for (Gene gene : this.getGenes()) {
      if (gene.isEnabled()) {
        conns.add(new GeneConnection(gene.getFrom(), gene.getTo()));
      }
    }
    return conns;
  }

  @Override
  public Genome clone() {
    Genome newGenome =
        new Genome(
            this.getSpecies(),
            this.getInputs(),
            this.getOutputs(),
            this.neatAlgorithmConfiguration,
            neatAlgorithm);

    newGenome.genes = new TreeMap<>();
    for (Map.Entry<Integer, Gene> s : this.genes.entrySet()) {
      newGenome.genes.put(s.getKey(), s.getValue().clone());
    }

    newGenome.inputNodes = new ArrayList<>(this.inputNodes);
    newGenome.outputNodes = new ArrayList<>(this.outputNodes);
    return newGenome;
  }

  public void fixDuplicates() {

    if (this.fitness != -1)
      throw new UnsupportedOperationException("fixDuplicates() must be called before getFitness()");

    for (Species sp : this.neatAlgorithm.getPopulation().getSpecies()) {
      for (Genome genome : sp.getMembers()) {
        List<GeneConnection> conA = this.getAllConnections();
        List<GeneConnection> conB = genome.getAllConnections();

        if (conA.equals(conB)) {
          Iterator<Gene> toCloneFrom = new ArrayList<>(genome.genes.values()).iterator();
          Iterator<Gene> toReplace = new ArrayList<>(this.genes.values()).iterator();

          while (toCloneFrom.hasNext() && toReplace.hasNext()) {
            Gene from = toCloneFrom.next();
            Gene to = toReplace.next();

            int oldInno = to.getInnovationNumber();
            int changeTo = from.getInnovationNumber();

            Gene old = this.genes.remove(oldInno);
            old.setInnovationNumber(changeTo);
            this.genes.put(old.getInnovationNumber(), old);
          }
          if (toCloneFrom.hasNext() || toReplace.hasNext()) throw new AssertionError();
          return;
        }
      }
    }
  }

  public static void crossAndAdd(Genome a, Genome b) {

    if (!a.getSpecies().equals(b.getSpecies()))
      throw new UnsupportedOperationException("Species must match when crossing");

    float aFitness = a.getFitness();
    float bFitness = b.getFitness();

    Genome strongest;
    Genome weakest;
    if (aFitness > bFitness) {
      strongest = a;
      weakest = b;
    } else {
      strongest = b;
      weakest = a;
    }
    Genome child = crossDominant(strongest, weakest);
    a.neatAlgorithm.getPopulation().addGenome(child);
  }

  private static Genome crossDominant(Genome dominant, Genome other) {
    if (!dominant.getSpecies().equals(other.getSpecies()))
      throw new UnsupportedOperationException("Species must match when crossing");

    if (dominant.getGenes().isEmpty() || other.getGenes().isEmpty())
      throw new UnsupportedOperationException("Genes may not be empty");

    int sharedLength = -1;
    for (int i = 1; ; i++) {
      if (i > 100000) throw new RuntimeException();

      if (dominant.hasGene(i) && other.hasGene(i)) {
        sharedLength = i;
      } else {
        break;
      }
    }
    if (sharedLength == -1) throw new AssertionError();

    Genome newGenome =
        new Genome(
            null,
            dominant.getInputs(),
            dominant.getOutputs(),
            dominant.neatAlgorithmConfiguration,
            dominant.neatAlgorithm);

    for (int i = 1; i <= dominant.getHighestInnovationNumber(); i++) {
      if (dominant.hasGene(i)) {
        if (other.hasGene(i)) {
          newGenome.addGene(
              RandomUtils.randomItem(new Gene[] {dominant.getGene(i), other.getGene(i)}),
              dominant,
              other);
        } else {
          newGenome.addGene(dominant.getGene(i), dominant, other);
        }
      }
    }

    newGenome.fixDuplicates();

    newGenome.mutate();

    return newGenome;
  }

  public void mutate() {
    MutationType.mutate(this);
  }

  public static float distance(Genome a, Genome b) {
    int aLength = a.getHighestInnovationNumber();
    int bLength = b.getHighestInnovationNumber();

    Genome longest;
    Genome shortest;

    if (aLength > bLength) {
      longest = a;
      shortest = b;
    } else {
      longest = b;
      shortest = a;
    }

    int shortestLength = shortest.getHighestInnovationNumber();
    int longestLength = longest.getHighestInnovationNumber();

    float disjoint = 0;
    float excess = 0;

    List<Float> weights = new ArrayList<>();
    for (int i = 1; i <= longestLength; i++) {
      Gene aa = longest.getGene(i);
      Gene bb = shortest.getGene(i);

      if ((aa == null && bb != null) || (aa != null && bb == null)) {

        if (i <= shortestLength) {
          disjoint++;
        } else {
          excess++;
        }
      }
      if (aa != null && bb != null) {
        float distance = Math.abs(aa.getWeight() - bb.getWeight());
        weights.add(distance);
      }
    }

    float total = 0;
    float size = 0;

    for (float w : weights) {
      total += w;
      size++;
    }

    float averageWeightDistance = total / size;
    float n = longest.getGenes().size();
    float c1 = a.neatAlgorithmConfiguration.getDistanceExcessWeight();
    float c2 = a.neatAlgorithmConfiguration.getDistanceDisjointWeight();
    float c3 = a.neatAlgorithmConfiguration.getDistanceWeightsWeight();

    return ((c1 * excess) / n) + ((c2 * disjoint) / n) + (c3 * averageWeightDistance);
  }

  public float[] calculate(float[] input) {
    Preconditions.checkArgument(
        input.length == this.getInputNodes().size(),
        "Input size must be equal to the given length.");
    Map<Integer, Float> inputValues = new HashMap<>();

    int i = 0;
    for (int inputNode : this.getInputNodes()) {
      inputValues.put(inputNode, input[i++]);
    }

    Map<Integer, Float> cache = new HashMap<>();
    i = 0;
    float[] out = new float[this.getOutputNodes().size()];
    for (int output : this.getOutputNodes()) {
      out[i++] = this.getOutput(output, cache, inputValues);
    }
    return out;
  }

  private float getOutput(int node, Map<Integer, Float> cache, Map<Integer, Float> inputValues) {

    Float val = cache.get(node);
    if (val != null) return val;

    float sum = 0;

    for (Gene gene : this.getGenes()) {
      if (gene.getTo() == node && gene.isEnabled()) {
        if (this.isInputNode(gene.getFrom())) {
          sum += inputValues.get(gene.getFrom()) * gene.getWeight();
        } else {
          sum += this.getOutput(gene.getFrom(), cache, inputValues) * gene.getWeight();
        }
      }
    }

    float calculatedOutput = this.neatAlgorithmConfiguration.getActivationFunction().apply(sum);
    cache.put(node, calculatedOutput);
    return calculatedOutput;
  }

  private float fitness = -1;

  public NeatAlgorithmConfiguration getNeatAlgorithmConfiguration() {
    return neatAlgorithmConfiguration;
  }

  private float calculateFitness() {
    this.fitness = this.neatAlgorithmConfiguration.getTrainingTask().apply(this::calculate);
    if (this.fitness > this.getSpecies().getHighestFitness()) {
      this.getSpecies().setHighestFitness(this.fitness);
    }
    return this.fitness;
  }

  public float getFitness() {
    if (this.fitness == -1) return calculateFitness();

    return this.fitness;
  }

  public static class GenomeSorter implements Comparator<Genome> {

    public int compare(Genome o1, Genome o2) {
      float a1 = o1.getFitness();
      float a2 = o2.getFitness();
      return Float.compare(a2, a1);
    }
  }

  public String toString() {
    StringBuilder genes = new StringBuilder();
    for (Map.Entry<Integer, Gene> gen : this.genes.entrySet()) {
      Gene gene = gen.getValue();
      genes
          .append("[ ")
          .append(gen.getKey())
          .append("=")
          .append(gene.getInnovationNumber())
          .append(" , ")
          .append(gene.getFrom())
          .append(" , ")
          .append(gene.getTo())
          .append(" , ")
          .append(gene.getWeight())
          .append(" ")
          .append(gene.isEnabled())
          .append(" ] ");
    }
    return genes.toString();
  }
}
