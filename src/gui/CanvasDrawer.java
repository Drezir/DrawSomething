/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;

/**
 *
 * @author Adam Ostrozlik
 */
public class CanvasDrawer {

    private final Canvas canvas;
    private final ColorPicker colorPicker;
    private final Slider slider;
    private double lastX, lastY;
    private boolean firstMove;
    private final GraphicsContext gc;
    private String lastCoordinates;

    public CanvasDrawer(Canvas canvas, ColorPicker colorPicket, Slider slider) {
        this.canvas = canvas;
        this.colorPicker = colorPicket;
        this.slider = slider;
        gc = canvas.getGraphicsContext2D();
        gc.setLineCap(StrokeLineCap.ROUND);
        firstMove = true;

    }

    void drawLine(MouseEvent event, Tool tool) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        if (tool == Tool.PEN) {
            gc.setStroke(colorPicker.getValue());
        } else {
            gc.setStroke(Color.WHITE);
        }
        gc.setLineWidth(slider.getValue());
        if (firstMove) {
            gc.strokeLine(event.getX(), event.getY(), event.getX(), event.getY());
            generateCoordinates(event.getX(), event.getY());
            firstMove = false;
        } else {
            gc.strokeLine(lastX, lastY, event.getX(), event.getY());
            generateCoordinates(lastX, lastY, event.getX(), event.getY());
        }
        lastX = event.getX();
        lastY = event.getY();
    }

    void releaseMouse(MouseEvent event) {
        if (event.getButton() != MouseButton.PRIMARY) {
            return;
        }
        firstMove = true;
    }

    public void initialState() {
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    String getData() {
        return getSettings() + "," + lastCoordinates;
    }

    private void generateCoordinates(double x, double y) {
        generateCoordinates(x, y, x, y);
    }

    private void generateCoordinates(double lastX, double lastY, double x, double y) {
        StringBuilder sb = new StringBuilder(30);
        sb.append(lastX);
        sb.append(",");
        sb.append(lastY);
        sb.append(",");
        sb.append(x);
        sb.append(",");
        sb.append(y);
        lastCoordinates = sb.toString();
    }

    public String getSettings() {
        return slider.getValue() + "," + colorPicker.getValue().toString();
    }

    void drawLine(Tool tool, double width, String color, double x1, double y1, double x2, double y2) {
        if (tool == Tool.PEN) {
            gc.setStroke(Color.valueOf(color));
        } else {
            gc.setStroke(Color.WHITE);
        }
        gc.setLineWidth(width);
        gc.strokeLine(x1, y1, x2, y2);
    }

    void resetCoordinates() {
        firstMove = true;
    }

}
