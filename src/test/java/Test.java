import de.jan_brh.neat.NeatAlgorithmBuilder;

public class Test {

  public static void main(String[] args) {
    NeatAlgorithmBuilder.newBuilder()
        .setInitAction(() -> {})
        .setClearAction(() -> {})
        .setNeatTask(
            () -> {
              System.out.println("Start");
              try {
                Thread.sleep(5000);
              } catch (InterruptedException e) {
                e.printStackTrace();
              }
              return 0f;
            })
        .build()
        .start();
  }
}
