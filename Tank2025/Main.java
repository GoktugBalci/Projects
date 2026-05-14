import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.*;

public class Main extends Application {

    private List<ImageView> wallTiles = new ArrayList<>();
    private List<Bullet> bullets = new ArrayList<>();
    private List<EnemyTank> enemyTanks = new ArrayList<>();
    private Image enemyImage;
    private List<Bullet> enemyBullets = new ArrayList<>();
    private long lastShotTime = 0;
    private final long shotCooldown = 300_000_000;
    private final Set<String> activeKeys = new HashSet<>();
    private int score = 0;
    private javafx.scene.text.Text scoreText;
    private boolean isPaused = false;
    private Pane pauseMenu;
    private AnimationTimer gameLoop;
    private boolean isGameOver = false;
    private Pane gameOverMenu;
    private int lives = 3;
    private javafx.scene.text.Text livesText;
    private javafx.scene.text.Text finalScoreText;


    @Override
    public void start(Stage primaryStage) {
        Pane root = new Pane();
        Scene scene = new Scene(root, 800, 600);
        scene.setFill(Color.BLACK);
        // Adding texts
        pauseMenu = new Pane();
        pauseMenu.setPrefSize(800, 600);
        pauseMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.6);");
        javafx.scene.text.Text pauseText = new javafx.scene.text.Text("Paused\n\nP - Resume\nR - Restart\nESC - Quit");
        pauseText.setFill(Color.WHITE);
        pauseText.setStyle("-fx-font-size: 30px; -fx-text-alignment: center;");
        pauseText.setLayoutX(300);
        pauseText.setLayoutY(350);
        pauseMenu.getChildren().add(pauseText);
        pauseMenu.setVisible(false);
        root.getChildren().add(pauseMenu);

        scoreText = new javafx.scene.text.Text("Score: 0");
        scoreText.setFill(Color.WHITE);
        scoreText.setStyle("-fx-font-size: 20px;");
        scoreText.setLayoutX(30);
        scoreText.setLayoutY(45);
        root.getChildren().add(scoreText);

        gameOverMenu = new Pane();
        finalScoreText = new javafx.scene.text.Text();
        gameOverMenu.setPrefSize(800, 600);
        gameOverMenu.setStyle("-fx-background-color: rgba(0, 0, 0, 0.75);");
        javafx.scene.text.Text gameOverText = new javafx.scene.text.Text("GAME OVER");
        gameOverText.setFill(Color.RED);
        gameOverText.setStyle("-fx-font-size: 48px;");
        gameOverText.setLayoutX(260);
        gameOverText.setLayoutY(220);
        javafx.scene.text.Text finalScoreText = new javafx.scene.text.Text();
        finalScoreText.setFill(Color.WHITE);
        finalScoreText.setStyle("-fx-font-size: 24px;");
        finalScoreText.setLayoutX(300);
        finalScoreText.setLayoutY(300);
        javafx.scene.text.Text instructionsText = new javafx.scene.text.Text("R - Restart\nESC - Quit");
        instructionsText.setFill(Color.LIGHTGRAY);
        instructionsText.setStyle("-fx-font-size: 24px;");
        instructionsText.setLayoutX(320);
        instructionsText.setLayoutY(350);
        gameOverMenu.getChildren().addAll(gameOverText, finalScoreText, instructionsText);
        gameOverMenu.setVisible(false);
        root.getChildren().add(gameOverMenu);

        livesText = new javafx.scene.text.Text("Lives: 3");
        livesText.setFill(Color.WHITE);
        livesText.setStyle("-fx-font-size: 20px;");
        livesText.setLayoutX(700);
        livesText.setLayoutY(45);
        root.getChildren().add(livesText);
        // Loading images from the src folder
        Image playerImage = new Image(Objects.requireNonNull(getClass().getResource("yellowTank1.png")).toExternalForm());
        Image playerImage1 = new Image(Objects.requireNonNull(getClass().getResource("yellowTank2.png")).toExternalForm());

        Image smallExplosionImage = new Image(Objects.requireNonNull(getClass().getResource("smallExplosion.png")).toExternalForm());
        Image bigExplosionImage = new Image(Objects.requireNonNull(getClass().getResource("explosion.png")).toExternalForm());

        enemyImage = new Image(Objects.requireNonNull(getClass().getResource("whiteTank1.png")).toExternalForm());
        Image enemyImage1 = new Image(Objects.requireNonNull(getClass().getResource("whiteTank2.png")).toExternalForm());

        Image wallImage = new Image(Objects.requireNonNull(getClass().getResource("wall.png")).toExternalForm());

        PlayerTank playerTank = new PlayerTank(playerImage, playerImage1, 380, 500);
        root.getChildren().add(playerTank.getImageView());
        //Adjusting the keys
        scene.setOnKeyPressed(event -> activeKeys.add(event.getCode().toString()));
        scene.setOnKeyReleased(event -> activeKeys.remove(event.getCode().toString()));
        scene.setOnKeyPressed(event -> {
            String code = event.getCode().toString();
            activeKeys.add(code);
            if (code.equals("P") && !isGameOver) {
                isPaused = !isPaused;
                pauseMenu.setVisible(isPaused);
            }
            if (isPaused || isGameOver) {
                if (code.equals("ESCAPE")) {
                    System.exit(0);
                } else if (code.equals("R")) {
                    restartGame((Stage) root.getScene().getWindow());
                }
            }
        });
        //Adding walls
        int wallSize = 20;
        for (int i = 0; i < 800 / wallSize; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(i * wallSize);
            wallTile.setLayoutY(0);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 800 / wallSize; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(i * wallSize);
            wallTile.setLayoutY(600 - wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 600 / wallSize; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(0);
            wallTile.setLayoutY(i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 600 / wallSize; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(800 - wallSize);
            wallTile.setLayoutY(i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 10; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(300 + i * wallSize);
            wallTile.setLayoutY(250);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 9; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(100);
            wallTile.setLayoutY(350 + i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }
        for (int i = 0; i < 5; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(200);
            wallTile.setLayoutY(350 + i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        for (int i = 0; i < 9; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(700);
            wallTile.setLayoutY(350 + i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }
        for (int i = 0; i < 5; i++) {
            ImageView wallTile = new ImageView(wallImage);
            wallTile.setFitWidth(wallSize);
            wallTile.setFitHeight(wallSize);
            wallTile.setLayoutX(600);
            wallTile.setLayoutY(350 + i * wallSize);
            wallTiles.add(wallTile);
            root.getChildren().add(wallTile);
        }

        gameLoop = new AnimationTimer() {
            @Override
            public void handle(long now) {
                List<EnemyTank> deadEnemies = new ArrayList<>();
                List<Bullet> bulletsToRemove = new ArrayList<>();
                if (isPaused || isGameOver) return;// For stopping the game when the game is over or paused
                double speed = 1;
                // Handling the player tank movements
                if (activeKeys.contains("UP") && playerTank.getY() - speed >= 20) {
                    playerTank.move(0, -speed);
                    playerTank.getImageView().setRotate(270);
                    if (isCollidingWithWall(playerTank)) {
                        playerTank.move(0, speed);
                    } else {
                        playerTank.getImageView().setRotate(270);
                        playerTank.animateWhileMoving();
                    }
                }
                else if (activeKeys.contains("DOWN") && playerTank.getY() + speed + 40 <= 580) {
                    playerTank.move(0, speed);
                    playerTank.getImageView().setRotate(90);
                    if (isCollidingWithWall(playerTank)) {
                        playerTank.move(0, -speed);
                    } else {
                        playerTank.getImageView().setRotate(90);
                        playerTank.animateWhileMoving();
                    }
                }
                else if (activeKeys.contains("LEFT") && playerTank.getX() - speed >= 20) {
                    playerTank.move(-speed, 0);
                    playerTank.getImageView().setRotate(180);
                    if (isCollidingWithWall(playerTank)) {
                        playerTank.move(speed, 0);
                    } else {
                        playerTank.getImageView().setRotate(180);
                        playerTank.animateWhileMoving();
                    }
                }
                else if (activeKeys.contains("RIGHT") && playerTank.getX() + speed + 40 <= 780) {
                    playerTank.move(speed, 0);
                    playerTank.getImageView().setRotate(0);
                    if (isCollidingWithWall(playerTank)) {
                        playerTank.move(-speed, 0); // Undo move
                    } else {
                        playerTank.getImageView().setRotate(0);
                        playerTank.animateWhileMoving();
                    }
                }
                //Handling shooting
                if (activeKeys.contains("X")) {
                    long currentTime = System.nanoTime();
                    if (currentTime - lastShotTime >= shotCooldown) {
                        lastShotTime = currentTime;

                        double angle = playerTank.getImageView().getRotate();
                        double bulletX = playerTank.getX() - 5;
                        double bulletY = playerTank.getY();

                        if (angle == 0) {
                            bulletX += 40;
                            bulletY += 15;
                        } else if (angle == 90) {
                            bulletX += 15;
                            bulletY += 40;
                        } else if (angle == 180) {
                            bulletX -= 10;
                            bulletY += 15;
                        } else if (angle == 270) {
                            bulletX += 15;
                            bulletY -= 10;
                        }

                        Bullet bullet = new Bullet(bulletX, bulletY, angle);
                        bullets.add(bullet);
                        root.getChildren().add(bullet.getImageView());
                    }
                }

                for (Bullet bullet : enemyBullets) {
                    bullet.update();
                    //Bullet wall collisions
                    for (ImageView wall : wallTiles) {
                        if (bullet.getImageView().getBoundsInParent().intersects(wall.getBoundsInParent())) {
                            bulletsToRemove.add(bullet);
                            showExplosion(root, smallExplosionImage, bullet.getX(), bullet.getY(), 20, Duration.millis(200));
                            break;
                        }
                    }
                    //Bullet player tank collisions
                    if (bullet.getImageView().getBoundsInParent().intersects(playerTank.getImageView().getBoundsInParent())) {
                        bulletsToRemove.add(bullet);
                        showExplosion(root, bigExplosionImage, playerTank.getX(), playerTank.getY(), 40, Duration.millis(400));
                        playerTank.setX(380);
                        playerTank.setY(500);
                        lives--;
                        livesText.setText("Lives: " + lives);
                        if (lives < 0) {
                            isGameOver = true;
                            finalScoreText.setText("Final Score: " + score);
                            gameLoop.stop();
                            gameOverMenu.setVisible(true);
                        } else {
                            playerTank.setX(380);
                            playerTank.setY(500);
                        }
                    }
                }

                for (Bullet b : bulletsToRemove) {
                    root.getChildren().remove(b.getImageView());
                }
                enemyBullets.removeAll(bulletsToRemove);
                bulletsToRemove.clear();

                for (EnemyTank enemy : enemyTanks) {
                    enemy.update(wallTiles);
                    // Cooldown part also adjusting the firing place of tanks
                    if (enemy.shouldShoot()) {
                        double angle = enemy.getImageView().getRotate();
                        double bulletX = enemy.getX() + 20;
                        double bulletY = enemy.getY() + 20;

                        double offset = 20;
                        if (angle == 0) {
                            bulletX += offset;
                        } else if (angle == 90) {
                            bulletY += offset;
                        } else if (angle == 180) {
                            bulletX -= offset;
                        } else if (angle == 270) {
                            bulletY -= offset;
                        }

                        Bullet bullet = new Bullet(bulletX - 5, bulletY - 5, angle);
                        enemyBullets.add(bullet);
                        root.getChildren().add(bullet.getImageView());
                    }

                }
                enemyTanks.removeAll(deadEnemies);

                //Adding new enemy tanks to spawn
                while (enemyTanks.size() < 6) {
                    double x = 40 + new Random().nextInt(720);
                    double y = 20;

                    EnemyTank newEnemy = new EnemyTank(enemyImage, enemyImage1, x, y);
                    enemyTanks.add(newEnemy);
                    root.getChildren().add(newEnemy.getImageView());
                }

                Iterator<Bullet> bulletIterator = bullets.iterator();
                while (bulletIterator.hasNext()) {
                    Bullet bullet = bulletIterator.next();
                    bullet.move();
                    boolean removeBullet = false;
                    //Bullet wall collision
                    for (ImageView wall : wallTiles) {
                        if (bullet.getImageView().getBoundsInParent().intersects(wall.getBoundsInParent())) {
                            showExplosion(root, smallExplosionImage, bullet.getX(), bullet.getY(), 20, Duration.millis(200));
                            removeBullet = true;
                            break;
                        }
                    }
                    //Enemy tanks player bullet collision
                    for (int i = 0; i < enemyTanks.size(); i++) {
                        EnemyTank enemy = enemyTanks.get(i);
                        if (bullet.getImageView().getBoundsInParent().intersects(enemy.getImageView().getBoundsInParent())) {
                            showExplosion(root, bigExplosionImage, enemy.getX(), enemy.getY(), 40, Duration.millis(400));
                            root.getChildren().remove(enemy.getImageView());
                            enemyTanks.remove(enemy);
                            score += 100;
                            scoreText.setText("Score: " + score);
                            removeBullet = true;
                            break;
                        }
                    }
                    if (removeBullet) {
                        root.getChildren().remove(bullet.getImageView());
                        bulletIterator.remove();
                    }
                }
            }
        };gameLoop.start();
        //Spawning enemies
        for (int i = 0; i < 6; i++) {
            spawnEnemy(root);
        }
        primaryStage.setTitle("Tank 2025");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    //Spawn enemy method
    private void spawnEnemy(Pane root) {
        Random random = new Random();
        double x = 40 + random.nextInt(700);
        double y = 20;
        enemyImage = new Image(Objects.requireNonNull(getClass().getResource("whiteTank1.png")).toExternalForm());
        Image enemyImage1 = new Image(Objects.requireNonNull(getClass().getResource("whiteTank2.png")).toExternalForm());
        EnemyTank enemy = new EnemyTank(enemyImage, enemyImage1 ,x, y);
        enemyTanks.add(enemy);
        root.getChildren().add(enemy.getImageView());
    }
    //For the showing explosions as their size
    private void showExplosion(Pane root, Image image, double x, double y, double size, Duration duration) {
        ImageView explosion = new ImageView(image);
        explosion.setFitWidth(size);
        explosion.setFitHeight(size);
        explosion.setLayoutX(x);
        explosion.setLayoutY(y);
        root.getChildren().add(explosion);

        PauseTransition pause = new PauseTransition(duration);
        pause.setOnFinished(e -> root.getChildren().remove(explosion));
        pause.play();
    }
    //For checking wall and player tank collision
    private boolean isCollidingWithWall(PlayerTank tank) {
        for (ImageView wall : wallTiles) {
            if (tank.getImageView().getBoundsInParent().intersects(wall.getBoundsInParent())) {
                return true;
            }
        }
        return false;
    }
    // To restart the game
    private void restartGame(Stage stage) {
        if (gameLoop != null) {
            gameLoop.stop();
        }
        lives = 3;
        wallTiles.clear();
        bullets.clear();
        enemyBullets.clear();
        enemyTanks.clear();
        activeKeys.clear();
        lastShotTime = 0;
        score = 0;
        isPaused = false;
        isGameOver = false;

        start(stage);
    }
    public static void main(String[] args) {
        launch(args);
    }
}
