import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyAdapter;
import org.ejml.simple.SimpleMatrix;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.Collections;
import java.util.Comparator;

public class Main {

    // main method
    public static void main(String[] args) {

        Dimension displayResolution = Toolkit.getDefaultToolkit().getScreenSize();
        int displayWidth_pixels = (int) displayResolution.getWidth();
        int displayHeight_pixels = (int) displayResolution.getHeight();
        double aspectRatio = (double) displayWidth_pixels / (double) displayHeight_pixels;
        Rectangle fullscreen = new Rectangle(0, 0, displayWidth_pixels, displayHeight_pixels);

        JFrame gameWindow = new JFrame("Cubes");
        gameWindow.setBounds(fullscreen);
        gameWindow.getContentPane().setBackground(Color.WHITE);
        gameWindow.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        double FOVangle = 120;
        double screenDistance = 0.05;
        double projWidth = 2 * screenDistance * Math.tan(FOVangle / 2);
        double projHeight = projWidth / aspectRatio;
        SimpleMatrix displayCentrePixel = new SimpleMatrix(
                new double[][] { { (double) displayWidth_pixels / 2 }, { (double) displayHeight_pixels / 2 } });

        SimpleMatrix unitY = new SimpleMatrix(new double[][] { { 0 }, { 1 }, { 0 } });

        SimpleMatrix cameraPos = new SimpleMatrix(new double[][] { { -9 }, { -9 }, { -9 } });
        SimpleMatrix cameraFacing = new SimpleMatrix(new double[][] { { 1 }, { 1 }, { 1 } });
        cameraFacing = cameraFacing.scale(1 / cameraFacing.normF());

        SimpleMatrix displayDx = matrixMath.crossProduct3x3(cameraFacing, unitY);
        SimpleMatrix displayDy = matrixMath.crossProduct3x3(cameraFacing, displayDx);
        displayDx = displayDx.scale(projWidth / displayWidth_pixels);
        displayDy = displayDy.scale(projHeight / displayHeight_pixels);

        Player camera = new Player(cameraPos, cameraFacing, true, displayDx, displayDy);

        ArrayList<Cube> Cubes = new ArrayList<Cube>();
        ArrayList<JComponent[]> CubeFaces = new ArrayList<JComponent[]>();

        // Add cubes here:

        // Floating Cubes
        Cubes.add(makeCube(2, 2, 2, Color.RED, 1, fullscreen));
        Cubes.add(makeCube(4, 6, 7, Color.BLUE, 1, fullscreen));
        Cubes.add(makeCube(3, 5, 1, Color.GREEN, 1, fullscreen));
        Cubes.add(makeCube(0, 4, 2, Color.YELLOW, 1, fullscreen));
        Cubes.add(makeCube(5, 6, 1, Color.PINK, 1, fullscreen));
        Cubes.add(makeCube(0, 2, 8, Color.ORANGE, 1, fullscreen));
        Cubes.add(makeCube(5, 5, 10, Color.CYAN, 1, fullscreen));
        Cubes.add(makeCube(5, 8, 2, Color.MAGENTA, 1, fullscreen));

        Cubes.add(makeCube(6, 1, 0, Color.ORANGE, 1, fullscreen));
        Cubes.add(makeCube(9, 0, 1, Color.PINK, 1, fullscreen));
        Cubes.add(makeCube(3, 0, 6, Color.GREEN, 1, fullscreen));
        Cubes.add(makeCube(7, 1, 5, Color.YELLOW, 1, fullscreen));
        Cubes.add(makeCube(3, 8, 5, Color.RED, 1, fullscreen));
        Cubes.add(makeCube(2, 10, 4, Color.BLUE, 1, fullscreen));

        /*
         * // Cube Pile
         * Cubes.add(makeCube(10, 0, 0, Color.RED, 1, fullscreen));
         * Cubes.add(makeCube(11, 0, 0, Color.BLUE, 1, fullscreen));
         * Cubes.add(makeCube(11, 0, 1, Color.GREEN, 1, fullscreen));
         * Cubes.add(makeCube(11, 1, 0, Color.YELLOW, 1, fullscreen));
         */

        /*
         * // 3x Varying sized cubes
         * Cubes.add(makeCube(13, -1, -1, Color.BLUE, 2, fullscreen));
         * Cubes.add(makeCube(15, -1, -1, Color.RED, 3, fullscreen));
         * Cubes.add(makeCube(14, -1, -5, Color.GREEN, 4, fullscreen));
         */

        for (int i = 0; i < Cubes.size(); i++) {
            CubeFaces.add(Cubes.get(i).renderProcessing(camera.facing, camera.position, screenDistance,
                    camera.displayDxDy, displayCentrePixel, fullscreen));
        }

        gameWindow.setVisible(true);
        long previousTime = System.nanoTime();
        long currentTime, timeChange;
        double frameDuration_ns = 1000000000.0 / 60.0;
        double mouseSensitivity = 0.00002;
        double[] mousePosRelative = new double[2];
        double moveSpeed = 0.1;
        SimpleMatrix moveDirection = new SimpleMatrix(new double[][] { { 0 }, { 0 }, { 0 } });
        boolean running = true;

        // key listeners
        Set<Integer> pressedKeys = new HashSet<>();
        gameWindow.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                pressedKeys.add(e.getKeyCode());
            }

            @Override
            public void keyReleased(KeyEvent e) {
                pressedKeys.remove(e.getKeyCode());
            }
        });

        while (running) {
            currentTime = System.nanoTime();
            timeChange = currentTime - previousTime;
            if (timeChange > frameDuration_ns) {

                // camera handling
                Point mousePos = MouseInfo.getPointerInfo().getLocation();
                mousePosRelative[0] = mousePos.getX() - (displayWidth_pixels / 2);
                mousePosRelative[1] = mousePos.getY() - (displayHeight_pixels / 2);
                if (Math.pow(mousePosRelative[0], 2) + Math.pow(mousePosRelative[1], 2) > 100000) {
                    camera.facing = adjustFacingDirection(mousePosRelative, mouseSensitivity, camera.facing);
                    camera.hasMoved = true;
                }

                // movement handling
                moveDirection = new SimpleMatrix(new double[][] { { 0 }, { 0 }, { 0 } });
                // process key presses
                if (pressedKeys.contains(KeyEvent.VK_W)) {
                    moveDirection = moveDirection.plus(camera.facing.scale(moveSpeed));
                }
                if (pressedKeys.contains(KeyEvent.VK_S)) {
                    moveDirection = moveDirection.plus(camera.facing.scale(-moveSpeed));
                }
                if (pressedKeys.contains(KeyEvent.VK_D)) {
                    moveDirection = moveDirection
                            .plus(camera.displayDx.scale(moveSpeed / camera.displayDx.normF()));
                }
                if (pressedKeys.contains(KeyEvent.VK_A)) {
                    moveDirection = moveDirection
                            .plus(camera.displayDx.scale(-moveSpeed / camera.displayDx.normF()));
                }
                if (pressedKeys.contains(KeyEvent.VK_SPACE)) {
                    moveDirection = moveDirection.plus(unitY.scale(moveSpeed));
                }
                if (pressedKeys.contains(KeyEvent.VK_SHIFT)) {
                    moveDirection = moveDirection.plus(unitY.scale(-moveSpeed));
                }
                // move player position
                if (moveDirection.normF() != 0) {
                    moveDirection.scale(moveSpeed / moveDirection.normF());
                    camera.position = camera.position.plus(moveDirection);
                    camera.hasMoved = true;
                }

                if (camera.hasMoved) {

                    camera.updateDisplay(projWidth, projHeight, displayWidth_pixels, displayHeight_pixels);

                    // Note: Sorting only works correctly for equal sized cubes
                    Collections.sort(Cubes, new Comparator<Cube>() {
                        @Override
                        public int compare(Cube c1, Cube c2) {
                            return Double.compare(c1.getDistance(camera.position), c2.getDistance(camera.position));
                        }
                    });

                    CubeFaces.clear();
                    for (int i = 0; i < Cubes.size(); i++) {
                        CubeFaces.add(i, Cubes.get(i).renderProcessing(camera.facing, camera.position, screenDistance,
                                camera.displayDxDy, displayCentrePixel, fullscreen));
                    }

                    // rendering
                    JComponent Coordinates = new JComponent() {
                        @Override
                        protected void paintComponent(Graphics g) {
                            super.paintComponent(g);
                            g.setColor(Color.BLACK);
                            g.drawString(Double.toString(camera.position.get(0, 0)), 50, 50);
                            g.drawString(Double.toString(camera.position.get(1, 0)), 50, 100);
                            g.drawString(Double.toString(camera.position.get(2, 0)), 50, 150);
                        }
                    };
                    Coordinates.setOpaque(false);
                    Coordinates.setBounds(fullscreen);

                    gameWindow.setLayout(null);
                    gameWindow.getContentPane().removeAll();
                    gameWindow.getContentPane().add(Coordinates);

                    // render cubes
                    for (int i = 0; i < CubeFaces.size(); i++) {
                        for (int j = 0; j < CubeFaces.get(i).length; j++) {
                            gameWindow.getContentPane().add(CubeFaces.get(i)[j]);
                        }
                    }
                    gameWindow.repaint();
                    previousTime = currentTime;
                    camera.hasMoved = false;
                }

            }
            // add fps limiter
            try {
                Thread.sleep(16);
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    public static Cube makeCube(double xPos, double yPos, double zPos, Color colour, double size,
            Rectangle fullscreen) {
        SimpleMatrix cubePos = new SimpleMatrix(new double[][] { { xPos }, { yPos }, { zPos } });
        Cube newCube = new Cube(cubePos, size, colour, fullscreen);
        return newCube;
    }

    public static SimpleMatrix adjustFacingDirection(double[] mousePos, double sensitivity,
            SimpleMatrix currentFacing) {

        double pitch = Math.asin(currentFacing.get(1));
        double yaw = Math.atan2(currentFacing.get(2), currentFacing.get(0));
        pitch -= mousePos[1] * sensitivity;
        yaw += mousePos[0] * sensitivity;

        double xFacing = Math.cos(pitch) * Math.cos(yaw);
        double yFacing = Math.sin(pitch);
        double zFacing = Math.cos(pitch) * Math.sin(yaw);

        SimpleMatrix newFacing = new SimpleMatrix(new double[][] { { xFacing }, { yFacing }, { zFacing } });
        newFacing = newFacing.scale(1 / newFacing.normF());
        return newFacing;
    }

}
