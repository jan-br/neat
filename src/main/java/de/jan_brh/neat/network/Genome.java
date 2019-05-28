package de.jan_brh.neat.network;

import java.util.Collection;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class Genome {

  private final IncrementalCounter geneCounter = new IncrementalCounter();
  private final IncrementalCounter geneConnectionCounter = new IncrementalCounter();
  private final Collection<GeneConnection> geneConnections = new CopyOnWriteArrayList<>();

  protected Collection<GeneConnection> getConnectionToFrontLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getTo().equals(gene))
        .collect(Collectors.toList());
  }

  protected Collection<GeneConnection> getConnectionsToNextLayer(Gene gene) {
    return this.geneConnections.stream()
        .filter(geneConnection -> geneConnection.getFrom().equals(gene))
        .collect(Collectors.toList());
  }
}
