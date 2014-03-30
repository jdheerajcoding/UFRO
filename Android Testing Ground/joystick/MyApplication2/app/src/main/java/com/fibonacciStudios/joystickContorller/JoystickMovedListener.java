package com.fibonacciStudios.joystickContorller;

/**
 * Created by Nathan on 2/7/14.
 */
public interface JoystickMovedListener {
    public void OnMoved(int pan, int tilt);
    public void OnReleased();
    public void OnReturnedToCenter();
}
