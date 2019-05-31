package de.jan_br.neat.network;

import de.jan_br.neat.NeatAlgorithmConfiguration;

import java.util.ArrayList;
import java.util.List;

public class Population {

	private final NeatAlgorithmConfiguration neatAlgorithmConfiguration;
	private final List<Species> species = new ArrayList<>();

	public Population(NeatAlgorithmConfiguration neatAlgorithmConfiguration) {
		this.neatAlgorithmConfiguration = neatAlgorithmConfiguration;
	}

	public List<Species> getSpecies() {
		return species;
	}

	public void addGenome(Genome genome) {
		Species species = this.classify(genome);
		species.getMembers().add(genome);
	}

	private Species classify(Genome genome) {
		for (Species existing : this.getSpecies()) {
			if (existing.isCompatible(genome)) {
				genome.setSpecies(existing);
				return existing;
			}
		}

		Species ge = new Species(neatAlgorithmConfiguration, genome);
		this.getSpecies().add(ge);

		return ge;
	}

	public Genome getBestPerforming() {
		Genome best = null;
		float bestFitness = -1;

		for (Species sp : this.species) {
			for (Genome g : sp.getMembers()) {
				if (best == null || g.getFitness() > bestFitness) {
					best = g;
					bestFitness = g.getFitness();
				}
			}
		}

		return best;

	}
}
