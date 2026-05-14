import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Objects;

public class Bullet {
    private ImageView imageView;
    private double dx, dy;

    public Bullet(double startX, double startY, double angleDegrees) {
        Image bulletImage = new Image(Objects.requireNonNull(getClass().getResource("bullet.png")).toExternalForm());

        imageView = new ImageView(bulletImage);
        imageView.setFitWidth(20);
        imageView.setFitHeight(8);
        imageView.setRotate(angleDegrees);
        imageView.setLayoutX(startX);
        imageView.setLayoutY(startY);

        double speed = 1;
        // bullet movement direction based on the angle
        if (angleDegrees == 0) {
            dx = speed; dy = 0;
        } else if (angleDegrees == 90) {
            dx = 0; dy = speed;
        } else if (angleDegrees == 180) {
            dx = -speed; dy = 0;
        } else if (angleDegrees == 270) {
            dx = 0; dy = -speed;
        }
    }

    public void move() {
        imageView.setLayoutX(imageView.getLayoutX() + dx);
        imageView.setLayoutY(imageView.getLayoutY() + dy);
    }

    public void update() {
        move();
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
}
