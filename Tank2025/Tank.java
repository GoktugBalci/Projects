import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Random;

public abstract class Tank {
    protected ImageView imageView;

    public Tank(Image image, double x, double y, double width, double height) {
        imageView = new ImageView(image);
        imageView.setFitHeight(height);
        imageView.setFitWidth(width);
        imageView.setLayoutX(x);
        imageView.setLayoutY(y);
    }

    public ImageView getImageView() {
        return imageView;
    }
    public double getX() {
        return imageView.getLayoutX();
    }
    public double getY() {
        return imageView.getLayoutY();
    }
    public void setX(double x) {
        imageView.setLayoutX(x);
    }
    public void setY(double y) {
        imageView.setLayoutY(y);
    }

    public void move(double dx, double dy) {
        imageView.setLayoutX(getX() + dx);
        imageView.setLayoutY(getY() + dy);
    }
}
class PlayerTank extends Tank {
    private Image image1;           // load 2 images for the animations
    private Image image2;
    private int animationTick = 0;

    public PlayerTank(Image image1, Image image2, double x, double y) {
        super(image1, x, y, 40, 40);
        this.image1 = image1;
        this.image2 = image2;
    }

    public void animateWhileMoving() {
        animationTick++;
        if (animationTick % 10 == 0) {
            imageView.setImage(imageView.getImage() == image1 ? image2 : image1);
        }
    }
}

class EnemyTank extends Tank {
    private final Random random = new Random();
    private int direction = random.nextInt(4);
    private int moveCounter = 0;
    private final int moveDuration = 60;
    private final double speed = 0.5;
    private long lastShotTime = 0;
    private final long shotCooldown = 2_000_000_000;
    private Image image1;// same for the animations
    private Image image2;
    private int animationTick = 0;
    private int shootCooldown = 0;

    public EnemyTank(Image image1, Image image2, double x, double y) {
        super(image1, x, y, 40, 40);
        this.image1 = image1;
        this.image2 = image2;
    }

    public void update(List<ImageView> wallTiles) {
        moveCounter++;

        double dx = 0, dy = 0;
        double angle = 0;
        // this part handles enemy movements randomly
        if (direction == 0 && getY() - speed > 20) {
            dy = -speed;
            angle = 270;
        } else if (direction == 1 && getY() + speed + 40 < 580) {
            dy = speed;
            angle = 90;
        } else if (direction == 2 && getX() - speed > 20) {
            dx = -speed;
            angle = 180;
        } else if (direction == 3 && getX() + speed + 40 < 780) {
            dx = speed;
            angle = 0;
        }

        move(dx, dy);
        imageView.setRotate(angle);
        animateWhileMoving();
        // for the wall collisions
        for (ImageView wall : wallTiles) {
            if (imageView.getBoundsInParent().intersects(wall.getBoundsInParent())) {
                move(-dx, -dy);
                direction = new Random().nextInt(4);
                break;
            }
        }
        if (moveCounter >= moveDuration) {
            direction = new Random().nextInt(4);
            moveCounter = 0;
        }
        shootCooldown++;
    }
    // Cooldown for shooting for enemy tanks
    public boolean shouldShoot() {
        long currentTime = System.nanoTime();
        if (currentTime - lastShotTime >= shotCooldown) {
            lastShotTime = currentTime;
            return true;
        }
        return false;
    }
    // For the animations
    public void animateWhileMoving() {
        animationTick++;
        if (animationTick % 10 == 0) {
            imageView.setImage(imageView.getImage() == image1 ? image2 : image1);
        }
    }
}


