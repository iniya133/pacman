package pacman;

public class Main {
    public static void main(String[] args) {
        Thread.currentThread().setUncaughtExceptionHandler((thread, throwable) -> {
            System.out.println("Handler caught exception: " + throwable.getMessage());
            System.exit(1);
        });

        Game game = new Game();
        UI ui = new UI();

        game.addObserver(ui);
        ui.bootstrap(game);
        System.exit(0);
    }
}
