package catan;

/*
 * Assignment 3 changes:
 * - added UNDO and REDO command types for command history interaction
 */
public enum CommandType {
    ROLL,
    GO,
    LIST,
    UNDO,
    REDO,
    BUILD_SETTLEMENT,
    BUILD_CITY,
    BUILD_ROAD
}