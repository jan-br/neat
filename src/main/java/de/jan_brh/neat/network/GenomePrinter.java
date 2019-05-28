package de.jan_brh.neat.network;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Collectors;

public class GenomePrinter {
  public static void printGenome(Genome genome, String path) {

    Random r = new Random();
    HashMap<Integer, Point> nodeGenePositions = new HashMap<>();
    int nodeSize = 40;
    int connectionSizeBulb = 6;
    int imageSize = 2048;

    BufferedImage image = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

    Graphics g = image.getGraphics();
    g.setColor(Color.WHITE);
    g.fillRect(0, 0, imageSize, imageSize);

    g.setColor(Color.BLUE);
    for (Gene gene : genome.getGenes()) {
      if (gene.getGeneType() == GeneType.INPUT) {
        float x =
            ((float) gene.getId() / ((float) countNodesByType(genome, GeneType.INPUT) + 1f))
                * imageSize;
        float y = imageSize - nodeSize / 2;
        g.fillOval((int) (x - nodeSize / 2), (int) (y - nodeSize / 2), nodeSize, nodeSize);
        nodeGenePositions.put(gene.getId(), new Point((int) x, (int) y));
      } else if (gene.getGeneType() == GeneType.HIDDEN) {
        int x = r.nextInt(imageSize - nodeSize * 2) + nodeSize;
        int y = r.nextInt(imageSize - nodeSize * 3) + (int) (nodeSize * 1.5f);
        g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        nodeGenePositions.put(gene.getId(), new Point(x, y));
      } else if (gene.getGeneType() == GeneType.OUTPUT) {
        int x =
            (int)
                (((float) (gene.getId() - countNodesByType(genome, GeneType.INPUT))
                        / ((float) countNodesByType(genome, GeneType.OUTPUT) + 1f))
                    * imageSize);

        int y = nodeSize / 2;
        g.fillOval(x - nodeSize / 2, y - nodeSize / 2, nodeSize, nodeSize);
        nodeGenePositions.put(gene.getId(), new Point(x, y));
      }
    }

    g.setColor(Color.BLACK);
    for (GeneConnection gene :
        genome.getGenes().stream()
            .flatMap(nodeGene -> genome.getConnectionsToNextLayer(nodeGene).stream())
            .collect(Collectors.toList())) {
      if (!gene.isEnabled()) {
        continue;
      }
      Point inNode = nodeGenePositions.get(gene.getFrom().getId());
      Point outNode = nodeGenePositions.get(gene.getTo().getId());

      Point lineVector =
          new Point((int) ((outNode.x - inNode.x) ), (int) ((outNode.y - inNode.y) * 0.99f));

      g.drawLine(inNode.x, inNode.y, inNode.x + lineVector.x, inNode.y + lineVector.y);
      g.fillRect(
          inNode.x + lineVector.x - connectionSizeBulb / 2,
          inNode.y + lineVector.y - connectionSizeBulb / 2,
          connectionSizeBulb,
          connectionSizeBulb);
      g.drawString(
          "" + gene.getWeight(),
          (int) (inNode.x + lineVector.x * 0.25f + 5),
          (int) (inNode.y + lineVector.y * 0.25f));
    }

    g.setColor(Color.WHITE);
    for (Gene nodeGene : genome.getGenes()) {
      Point p = nodeGenePositions.get(nodeGene.getId());
      g.drawString(
          "" + nodeGene.getId(), p.x - 3 * String.valueOf(nodeGene.getId()).length(), p.y + 5);
    }

    try {
      File file = new File(path);
      file.getAbsoluteFile().getParentFile().mkdirs();
      if (!file.exists()) {
        file.createNewFile();
      }
      ImageIO.write(image, "PNG", file);
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  private static int countNodesByType(Genome genome, GeneType type) {
    int c = 0;
    for (Gene node : genome.getGenes()) {
      if (node.getGeneType() == type) {
        c++;
      }
    }
    return c;
  }
}
