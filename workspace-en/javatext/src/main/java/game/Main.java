package game;

import java.util.ArrayList;

public class Main {
    private static ArrayList<Game> playedGames = new ArrayList<>();
    private static boolean quit = false;

    private static void play() {
        System.out.print("Enter your name: ");
        System.out.flush();
        String name = UserInterface.readString();
        Game game = new Game(name);
        game.run();
        playedGames.add(game);
    }

    public static void main(String[] args) {
        while (!quit) {
            System.out.println("Choose an option:");
            int choice = UserInterface.readChoice(new String[] {
                    "1. Play game",
                    "2. View high scores",
                    "3. View high scores for specific player",
                    "4. Quit"
            });

            switch (choice) {
                case 1:
                    play();
                    break;
                case 2:
                    UserInterface.showHighScores(playedGames);
                    break;
                case 3:
                    System.out.print("Enter the player name: ");
                    System.out.flush();
                    UserInterface.showHighScores(playedGames, UserInterface.readString());
                    break;
                case 4:
                    quit = true;
                    break;
            }
        }
    }
}
