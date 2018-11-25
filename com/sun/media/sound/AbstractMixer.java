/*
 * Copyright (c) 1999, 2007, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.sun.media.sound;

import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Mixer;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

/**
 * Abstract Mixer.  Implements Mixer (with abstract methods) and specifies
 * some other common methods for use by our implementation.
 *
 * @author Kara Kytle
 */
//$$fb 2002-07-26: let AbstractMixer be an AbstractLine and NOT an AbstractDataLine!
abstract class AbstractMixer extends AbstractLine implements Mixer {

    //  STATIC VARIABLES
    protected static final int PCM  = 0;
    protected static final int ULAW = 1;
    protected static final int ALAW = 2;


    // IMMUTABLE PROPERTIES

    /**
     * Info object describing this mixer.
     */
    private final Mixer.Info mixerInfo;

    /**
     * source lines provided by this mixer
     */
    protected Line.Info[] sourceLineInfo;

    /**
     * target lines provided by this mixer
     */
    protected Line.Info[] targetLineInfo;

    /**
     * if any line of this mixer is started
     */
    private boolean started = false;

    /**
     * if this mixer had been opened manually with open()
     * If it was, then it won't be closed automatically,
     * only when close() is called manually.
     */
    private boolean manuallyOpened = false;


    /**
     * Supported formats for the mixer.
     */
    //$$fb DELETE
    //protected Vector formats = new Vector();


    // STATE VARIABLES


    /**
     * Source lines (ports) currently open
     */
    protected Vector sourceLines = new Vector();


    /**
     * Target lines currently open.
     */
    protected Vector targetLines = new Vector();


    /**
     * Constructs a new AbstractMixer.
     * @param mixer the mixer with which this line is associated
     * @param controls set of supported controls
     */
    protected AbstractMixer(Mixer.Info mixerInfo,
                            Control[] controls,
                            Line.Info[] sourceLineInfo,
                            Line.Info[] targetLineInfo) {

        // Line.Info, AbstractMixer, Control[]
        super(new Line.Info(Mixer.class), null, controls);

        // setup the line part
        this.mixer = this;
        if (controls == null) {
            controls = new Control[0];
        }

        // setup the mixer part
        this.mixerInfo = mixerInfo;
        this.sourceLineInfo = sourceLineInfo;
        this.targetLineInfo = targetLineInfo;
    }


    // MIXER METHODS


    public Mixer.Info getMixerInfo() {
        return mixerInfo;
    }


    public Line.Info[] getSourceLineInfo() {
        Line.Info[] localArray = new Line.Info[sourceLineInfo.length];
        System.arraycopy(sourceLineInfo, 0, localArray, 0, sourceLineInfo.length);
        return localArray;
    }


    public Line.Info[] getTargetLineInfo() {

        Line.Info[] localArray = new Line.Info[targetLineInfo.length];
        System.arraycopy(targetLineInfo, 0, localArray, 0, targetLineInfo.length);
        return localArray;
    }


    public Line.Info[] getSourceLineInfo(Line.Info info) {

        int i;
        Vector vec = new Vector();

        for (i = 0; i < sourceLineInfo.length; i++) {

            if (info.matches(sourceLineInfo[i])) {
                vec.addElement(sourceLineInfo[i]);
            }
        }

        Line.Info[] returnedArray = new Line.Info[vec.size()];
        for (i = 0; i < returnedArray.length; i++) {
            returnedArray[i] = (Line.Info)vec.elementAt(i);
        }

        return returnedArray;
    }


    public Line.Info[] getTargetLineInfo(Line.Info info) {

        int i;
        Vector vec = new Vector();

        for (i = 0; i < targetLineInfo.length; i++) {

            if (info.matches(targetLineInfo[i])) {
                vec.addElement(targetLineInfo[i]);
            }
        }

        Line.Info[] returnedArray = new Line.Info[vec.size()];
        for (i = 0; i < returnedArray.length; i++) {
            returnedArray[i] = (Line.Info)vec.elementAt(i);
        }

        return returnedArray;
    }


    public boolean isLineSupported(Line.Info info) {

        int i;

        for (i = 0; i < sourceLineInfo.length; i++) {

            if (info.matches(sourceLineInfo[i])) {
                return true;
            }
        }

        for (i = 0; i < targetLineInfo.length; i++) {

            if (info.matches(targetLineInfo[i])) {
                return true;
            }
        }

        return false;
    }


    public abstract Line getLine(Line.Info info) throws LineUnavailableException;

    public abstract int getMaxLines(Line.Info info);

    protected abstract void implOpen() throws LineUnavailableException;
    protected abstract void implStart();
    protected abstract void implStop();
    protected abstract void implClose();


    public Line[] getSourceLines() {

        Line[] localLines;

        synchronized(sourceLines) {

            localLines = new Line[sourceLines.size()];

            for (int i = 0; i < localLines.length; i++) {
                localLines[i] = (Line)sourceLines.elementAt(i);
            }
        }

        return localLines;
    }


    public Line[] getTargetLines() {

        Line[] localLines;

        synchronized(targetLines) {

            localLines = new Line[targetLines.size()];

            for (int i = 0; i < localLines.length; i++) {
                localLines[i] = (Line)targetLines.elementAt(i);
            }
        }

        return localLines;
    }


    /**
     * Default implementation always throws an exception.
     */
    public void synchronize(Line[] lines, boolean maintainSync) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }


    /**
     * Default implementation always throws an exception.
     */
    public void unsynchronize(Line[] lines) {
        throw new IllegalArgumentException("Synchronization not supported by this mixer.");
    }


    /**
     * Default implementation always returns false.
     */
    public boolean isSynchronizationSupported(Line[] lines, boolean maintainSync) {
        return false;
    }


    // OVERRIDES OF ABSTRACT DATA LINE METHODS

    /**
     * This implementation tries to open the mixer with its current format and buffer size settings.
     */
    public synchronized void open() throws LineUnavailableException {
        open(true);
    }

    /**
     * This implementation tries to open the mixer with its current format and buffer size settings.
     */
    protected synchronized void open(boolean manual) throws LineUnavailableException {
        if (Printer.trace) Printer.trace(">> AbstractMixer: open()");
        if (!isOpen()) {
            implOpen();
            // if the mixer is not currently open, set open to true and send event
            setOpen(true);
            if (manual) {
                manuallyOpened = true;
            }
        }

        if (Printer.trace) Printer.trace("<< AbstractMixer: open() succeeded");
    }


    // METHOD FOR INTERNAL IMPLEMENTATION USE


    /**
     * The default implementation of this method just determines whether
     * this line is a source or target line, calls open(no-arg) on the
     * mixer, and adds the line to the appropriate vector.
     * The mixer may be opened at a format different than the line's
     * format if it is a DataLine.
     */
    protected synchronized void open(Line line) throws LineUnavailableException {

        if (Printer.trace) Printer.trace(">> AbstractMixer: open(line = " + line + ")");

        // $$kk: 06.11.99: ignore ourselves for now
        if (this.equals(line)) {
            if (Printer.trace) Printer.trace("<< AbstractMixer: open(" + line + ") nothing done");
            return;
        }

        // source line?
        if (isSourceLine(line.getLineInfo())) {
            if (! sourceLines.contains(line) ) {
                // call the no-arg open method for the mixer; it should open at its
                // default format if it is not open yet
                open(false);

                // we opened successfully! add the line to the list
                sourceLines.addElement(line);
            }
        } else {
            // target line?
            if(isTargetLine(line.getLineInfo())) {
                if (! targetLines.contains(line) ) {
                    // call the no-arg open method for the mixer; it should open at its
                    // default format if it is not open yet
                    open(false);

                    // we opened successfully!  add the line to the list
                    targetLines.addElement(line);
                }
            } else {
                if (Printer.err) Printer.err("Unknown line received for AbstractMixer.open(Line): " + line);
            }
        }

        if (Printer.trace) Printer.trace("<< AbstractMixer: open(" + line + ") completed");
    }


    /**
     * Removes this line from the list of open source lines and
     * open target lines, if it exists in either.
     * If the list is now empty, closes the mixer.
     */
    protected synchronized void close(Line line) {

        if (Printer.trace) Printer.trace(">> AbstractMixer: close(" + line + ")");

        // $$kk: 06.11.99: ignore ourselves for now
        if (this.equals(line)) {
            if (Printer.trace) Printer.trace("<< AbstractMixer: close(" + line + ") nothing done");
            return;
        }

        sourceLines.removeElement(line);
        targetLines.removeElement(line);

        if (Printer.debug) Printer.debug("AbstractMixer: close(line): sourceLines.size() now: " + sourceLines.size());
        if (Printer.debug) Printer.debug("AbstractMixer: close(line): targetLines.size() now: " + targetLines.size());


        if (sourceLines.isEmpty() && targetLines.isEmpty() && !manuallyOpened) {
            if (Printer.trace) Printer.trace("AbstractMixer: close(" + line + "): need to close the mixer");
            close();
        }

        if (Printer.trace) Printer.trace("<< AbstractMixer: close(" + line + ") succeeded");
    }


    /**
     * Close all lines and then close this mixer.
     */
    public synchronized void close() {
        if (Printer.trace) Printer.trace(">> AbstractMixer: close()");
        if (isOpen()) {
            // close all source lines
            Line[] localLines = getSourceLines();
            for (int i = 0; i<localLines.length; i++) {
                localLines[i].close();
            }

            // close all target lines
            localLines = getTargetLines();
            for (int i = 0; i<localLines.length; i++) {
                localLines[i].close();
            }

            implClose();

            // set the open state to false and send events
            setOpen(false);
        }
        manuallyOpened = false;
        if (Printer.trace) Printer.trace("<< AbstractMixer: close() succeeded");
    }

    /**
     * Starts the mixer.
     */
    protected synchronized void start(Line line) {

        if (Printer.trace) Printer.trace(">> AbstractMixer: start(" + line + ")");

        // $$kk: 06.11.99: ignore ourselves for now
        if (this.equals(line)) {
            if (Printer.trace) Printer.trace("<< AbstractMixer: start(" + line + ") nothing done");
            return;
        }

        // we just start the mixer regardless of anything else here.
        if (!started) {
            if (Printer.debug) Printer.debug("AbstractMixer: start(line): starting the mixer");
            implStart();
            started = true;
        }

        if (Printer.trace) Printer.trace("<< AbstractMixer: start(" + line + ") succeeded");
    }


    /**
     * Stops the mixer if this was the last running line.
     */
    protected synchronized void stop(Line line) {

        if (Printer.trace) Printer.trace(">> AbstractMixer: stop(" + line + ")");

        // $$kk: 06.11.99: ignore ourselves for now
        if (this.equals(line)) {
            if (Printer.trace) Printer.trace("<< AbstractMixer: stop(" + line + ") nothing done");
            return;
        }

        Vector localSourceLines = (Vector)sourceLines.clone();
        for (int i = 0; i < localSourceLines.size(); i++) {

            // if any other open line is running, return

            // this covers clips and source data lines
            if (localSourceLines.elementAt(i) instanceof AbstractDataLine) {
                AbstractDataLine sourceLine = (AbstractDataLine)localSourceLines.elementAt(i);
                if ( sourceLine.isStartedRunning() && (!sourceLine.equals(line)) ) {
                    if (Printer.trace) Printer.trace("<< AbstractMixer: stop(" + line + ") found running sourceLine: " + sourceLine);
                    return;
                }
            }
        }

        Vector localTargetLines = (Vector)targetLines.clone();
        for (int i = 0; i < localTargetLines.size(); i++) {

            // if any other open line is running, return
            // this covers target data lines
            if (localTargetLines.elementAt(i) instanceof AbstractDataLine) {
                AbstractDataLine targetLine = (AbstractDataLine)localTargetLines.elementAt(i);
                if ( targetLine.isStartedRunning() && (!targetLine.equals(line)) ) {
                    if (Printer.trace) Printer.trace("<< AbstractMixer: stop(" + line + ") found running targetLine: " + targetLine);
                    return;
                }
            }
        }

        // otherwise, stop
        if (Printer.debug) Printer.debug("AbstractMixer: stop(line): stopping the mixer");
        started = false;
        implStop();

        if (Printer.trace) Printer.trace("<< AbstractMixer: stop(" + line + ") succeeded");
    }



    /**
     * Determines whether this is a source line for this mixer.
     * Right now this just checks whether it's supported, but should
     * check whether it actually belongs to this mixer....
     */
    boolean isSourceLine(Line.Info info) {

        for (int i = 0; i < sourceLineInfo.length; i++) {
            if (info.matches(sourceLineInfo[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     * Determines whether this is a target line for this mixer.
     * Right now this just checks whether it's supported, but should
     * check whether it actually belongs to this mixer....
     */
    boolean isTargetLine(Line.Info info) {

        for (int i = 0; i < targetLineInfo.length; i++) {
            if (info.matches(targetLineInfo[i])) {
                return true;
            }
        }

        return false;
    }


    /**
     * Returns the first complete Line.Info object it finds that
     * matches the one specified, or null if no matching Line.Info
     * object is found.
     */
    Line.Info getLineInfo(Line.Info info) {
        if (info == null) {
            return null;
        }
        // $$kk: 05.31.99: need to change this so that
        // the format and buffer size get set in the
        // returned info object for data lines??
        for (int i = 0; i < sourceLineInfo.length; i++) {
            if (info.matches(sourceLineInfo[i])) {
                return sourceLineInfo[i];
            }
        }

        for (int i = 0; i < targetLineInfo.length; i++) {
            if (info.matches(targetLineInfo[i])) {
                return targetLineInfo[i];
            }
        }

        return null;
    }

}
