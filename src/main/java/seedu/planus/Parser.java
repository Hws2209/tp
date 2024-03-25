package seedu.planus;

import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * The Parser class handles the parsing of user commands in the PlaNus application.
 */
public class Parser {
    private static final Logger logger = Logger.getLogger("myLogger");


    /**
     * Parses the user command and performs the corresponding action on the timetable.
     * @param line The user command to be parsed.
     * @param timetable The timetable to be modified.
     * @return A boolean indicating whether the application should exit.
     */
    public static boolean parseCommand(String line, Timetable timetable) throws Exception {
        assert !line.isEmpty() : "Command line input should not be empty";
        assert timetable != null : "Timetable object should not be null";
        logger.log(Level.INFO, "Processing command: {0}", line);
        String[] words = line.split(" ");
        String[] yearAndTerm;
        int year;
        int term;

        String commandWord;
        try {
            commandWord = words[0].toLowerCase();
        } catch (IndexOutOfBoundsException | NullPointerException e) {
            logger.log(Level.WARNING, "Invalid command format: {0}", line);
            throw new Exception(Ui.INVALID_COMMAND);
        }
        switch(commandWord) {
        case "init":
            try {
                Timetable newTimetable = Storage.loadTimetable(words[1]);
                Storage.writeToFile(newTimetable);
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                throw new Exception(Ui.MISSING_MAJOR);
            }
            return false;
        case "add":
            String targetAdded;
            try {
                targetAdded = words[1];
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                logger.log(Level.WARNING, "Invalid command format: {0}", line);
                throw new Exception(Ui.INVALID_COMMAND);
            }
            if (targetAdded.equalsIgnoreCase("course")) {
                Course newCourse;
                String[] courseCodeAndYearAndTerms;
                try {
                    courseCodeAndYearAndTerms = words[2].split("y/", 2);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_ADD_COURSE);
                }
                String courseCode;
                try {
                    courseCode = courseCodeAndYearAndTerms[0].trim();
                    yearAndTerm = courseCodeAndYearAndTerms[1].split("t/", 2);
                    year = Integer.parseInt(yearAndTerm[0].trim());
                    term = Integer.parseInt(yearAndTerm[1].trim());
                } catch (NumberFormatException | IndexOutOfBoundsException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_ADD_COURSE);
                }
                String courseName = "userAdded";
                newCourse = new Course(courseCode, courseName, year, term);
                try {
                    logger.log(Level.INFO, "Adding course to timetable");
                    timetable.addCourse(newCourse);
                    Storage.writeToFile(timetable);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            } else if (targetAdded.equalsIgnoreCase("grade")) {
                try {
                    logger.log(Level.INFO, "Adding grade to course");
                    String courseCode = words[2];
                    String grade = words[3].toUpperCase(); // Convert grade to uppercase
                    timetable.addGrade(courseCode, grade);
                    Storage.writeToFile(timetable);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_ADD_GRADE);
                }
            } else {
                throw new Exception(Ui.INVALID_ADD);
            }
            return false;
        case "rm":
            String targetRemoved;
            try {
                targetRemoved = words[1];
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                logger.log(Level.WARNING, "Invalid command format: {0}", line);
                throw new Exception(Ui.INVALID_COMMAND);
            }
            if (targetRemoved.equalsIgnoreCase("course")) {
                try {
                    logger.log(Level.INFO, "Removing course from timetable");
                    timetable.removeCourse(words[2]);
                    Storage.writeToFile(timetable);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_REMOVE_COURSE);
                }
            } else if (targetRemoved.equalsIgnoreCase("grade")) {
                try {
                    logger.log(Level.INFO, "Removing grade from course");
                    timetable.removeGrade(words[2]);
                    Storage.writeToFile(timetable);
                } catch (IndexOutOfBoundsException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_REMOVE_GRADE);
                }
            } else {
                throw new Exception(Ui.INVALID_REMOVE);
            }
            return false;
        case "change":
            try {
                timetable.addGrade(words[2], words[3]);
                Storage.writeToFile(timetable);
            } catch (IndexOutOfBoundsException | NullPointerException e) {
                throw new Exception(Ui.INVALID_CHANGE_GRADE);
            }
            return false;
        case "check":
            if (words.length == 1) {
                System.out.println(GradeChecker.checkGrade(timetable));
            } else if (words.length == 2) {
                try {
                    year = Integer.parseInt(words[1].substring("y/".length()));
                } catch (NumberFormatException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_CHECK_YEAR_GRADE);
                }
                System.out.println(GradeChecker.checkGrade(timetable, year));
            } else {
                try {
                    year = Integer.parseInt(words[1].substring("y/".length()));
                    term = Integer.parseInt(words[2].substring("t/".length()));
                } catch (NumberFormatException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_CHECK_TERM_GRADE);
                }
                System.out.println(GradeChecker.checkGrade(timetable, year, term));
            }
            return false;
        case "view":
            if (words.length == 1) {
                System.out.println(PlanGetter.getPlan(timetable));
            } else if (words.length == 2) {
                try {
                    year = Integer.parseInt(words[1].substring("y/".length()));
                } catch (NumberFormatException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_VIEW_YEAR_PLAN);
                }
                System.out.println(PlanGetter.getPlan(timetable, year));
            } else {
                try {
                    year = Integer.parseInt(words[1].substring("y/".length()));
                    term = Integer.parseInt(words[2].substring("t/".length()));
                } catch (NumberFormatException | NullPointerException e) {
                    throw new Exception(Ui.INVALID_VIEW_TERM_PLAN);
                }
                System.out.println(PlanGetter.getPlan(timetable, year, term));
            }
            return false;
        case "help":
            Ui.printHelp();
            return false;
        case "bye":
            logger.log(Level.INFO, "Exiting PlaNus");
            return true;
        default:
            logger.log(Level.WARNING, "Invalid command format: {0}", line);
            throw new Exception(Ui.INVALID_COMMAND);
        }
    }
}
