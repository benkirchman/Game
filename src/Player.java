import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
public class Player extends GameObject {

    enum Direction {
        UP,
        DOWN,
        LEFT,
        RIGHT,
    }

    Sprite sprite;

    Direction direction;

    Point2D pointFacing;

    int moveSpeed = 5;
    int sprintModifier = 5;
    Point2D.Double playerDirection;

    Player(double x, double y, double width, double height) {
        super(x, y, width, height, 200);

        this.sprite = new Sprite(Sprite.SpriteType.PLAYER);

        this.pointFacing = new Point2D.Double(1, 1);

        this.direction = Direction.RIGHT;

    }

    void move(double x, double y, ArrayList<GameObject> objects) {
        super.move(x,y,objects);
        if (x > 0)
            this.direction = Direction.RIGHT;
        else if (x < 0)
            this.direction = Direction.LEFT;

        else if (y > 0)
            this.direction = Direction.DOWN;

        else if (y < 0)
            this.direction = Direction.UP;
    }

    void setDirection(Point2D.Double direction) {
        this.playerDirection = direction;
    }

    double getPlayerAngle() {
        Point2D.Double playerLoc = new Point2D.Double(getX(), getY());
        Point2D.Double toTheRight = new Point2D.Double(getX() + 1, 0);
        return VectorUtils.getThetaBetweenVectors(toTheRight, playerDirection, playerLoc);
    }

    Point2D getFrontFace() {
        // this is the gun
        Shape gun = sprite.spriteShapes[4];

        return new Point2D.Double(gun.getBounds2D().getCenterX(), gun.getBounds2D().getCenterY());
    }

    public void setPointFacing(Point2D pointFacing) {
        this.pointFacing = pointFacing;
    }

//    public void setCurrentAnimation(Animation animation) {
//        this.currentAnimation = animation;
//    }

    double imageRotationAngle() {
//        return Math.toRadians(10);
//         P0 object center
        double p0x = getX();
        double p0y = getY();

        // P1 object front
        double p1x = getFrontFace().getX();
        double p1y = getFrontFace().getY();

        // P2 point facing (mouse location)
        double p2x = pointFacing.getX();
        double p2y = pointFacing.getY();

        // PA vector P1 - P0
        double pax = p1x - p0x;
        double pay = p1y - p0y;

        // PB vector P2 - P0
        double pbx = p2x - p0x;
        double pby = p2y - p0y;

        double pa_dot_pb = (pax * pbx) + (pbx + pby);
        double mag_pa    = Math.sqrt(Math.pow(pax, 2) + Math.pow(pay, 2));
        double mag_pb    = Math.sqrt(Math.pow(pbx, 2) + Math.pow(pby, 2));

        double mags = mag_pa * mag_pb;

        double dot_over_mags = pa_dot_pb / mags;

        double theta = Math.toDegrees(Math.acos(dot_over_mags));

        return theta;
    }

    void setMoveSpeed(int moveSpeed) {
        this.moveSpeed = moveSpeed;
    }

    int getMoveSpeed() {
        return this.moveSpeed;
    }

    public BufferedImage rotateImageByDegrees(BufferedImage img, double angle) {

        double rads = Math.toRadians(angle);
        double sin = Math.abs(Math.sin(rads)), cos = Math.abs(Math.cos(rads));
        int w = img.getWidth();
        int h = img.getHeight();
        int newWidth = (int) Math.floor(w * cos + h * sin);
        int newHeight = (int) Math.floor(h * cos + w * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();
        AffineTransform at = new AffineTransform();
        at.translate((newWidth - w) / 2, (newHeight - h) / 2);

        int x = w / 2;
        int y = h / 2;

        at.rotate(rads, x, y);

        g2d.setTransform(at);
        g2d.drawImage(img, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    @Override
    boolean canMove(double x, double y, ArrayList<GameObject> others) {
        for(GameObject other : others) {
            // translate intersect to the top left.
            if (!(other instanceof Enemy) && other != this && other.isColliding() && isColliding() && this.intersects(getX() + x - getWidth()/2, getY() + y - getHeight()/2, other)) {
                return false;
            }
        }
        return true;
    }

    @Override
    Image getImage() {
        sprite.rotate(getPlayerAngle());
        return sprite.getImage();
    }
}
