package seedu.addressbook;

/*
 * NOTE : =============================================================
 * This class is written in a procedural fashion (i.e. not Object-Oriented)
 * Yes, it is possible to write non-OO code using an OO language.
 * ====================================================================
 */
//PROJECT TODO: Generic Parameter Collection
//PROJECT TODO: Password
//PROJECT TODO: Dynamic fields
//PROJECT TODO: Filter
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.InvalidPathException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Pattern;

/*
 * NOTE : =============================================================
 * This class header comment below is brief because details of how to
 * use this class are documented elsewhere.
 * ====================================================================
 */

/**
 * This class is used to maintain a list of person data which are saved
 * in a text file.
 **/
public class AddressBook {

    /**
     * Default file path used if the user doesn't provide the file name.
     */
    private static final String DEFAULT_STORAGE_FILEPATH = "addressbook.txt";

    /**
     * Version info of the program.
     */
    private static final String VERSION = "AddessBook Level 1 - Version 1.0";

    /**
     * A decorative prefix added to the beginning of lines printed by AddressBook
     */
    private static final String LINE_PREFIX = "|| ";

    /**
     * A platform independent line separator.
     */
    private static final String LS = System.lineSeparator() + LINE_PREFIX;

    /*
     * NOTE : ==================================================================
     * These messages shown to the user are defined in one place for convenient
     * editing and proof reading. Such messages are considered part of the UI
     * and may be subjected to review by UI experts or technical writers. Note
     * that Some of the strings below include '%1$s' etc to mark the locations
     * at which java String.format(...) method can insert values.
     * =========================================================================
     */
    private static final String MESSAGE_PROMPT_USER_INPUT = "Enter command: ";
    private static final String MESSAGE_ADDED = "New person added: %1$s";
    private static final String MESSAGE_ADDRESSBOOK_CLEARED = "Address book has been cleared!";
    private static final String MESSAGE_COMMAND_HELP = "%1$s: %2$s";
    private static final String MESSAGE_COMMAND_HELP_PARAMETERS = "\tParameters: %1$s";
    private static final String MESSAGE_COMMAND_HELP_EXAMPLE = "\tExample: %1$s";
    private static final String MESSAGE_EDIT_PERSON_SUCCESS = "Old Person: %1$s "
                                                            + LS + "\tNew Person: %2$s";
    private static final String MESSAGE_DELETE_PERSON_SUCCESS = "Deleted Person: %1$s";
    private static final String MESSAGE_UNDO_REDO_SUCCESS = "Successfully completed %1$s operation. Changes:";
    private static final String MESSAGE_DISPLAY_PERSON_DATA = "%1$s  Phone Number: %2$s  Email: %3$s";
    private static final String MESSAGE_DISPLAY_LIST_ELEMENT_INDEX = "%1$d. ";
    private static final String MESSAGE_GOODBYE = "Exiting Address Book... Good bye!";
    private static final String MESSAGE_APPLICATION_ERROR = "Application Error: %1$s command was unable to be resolved.";
    private static final String MESSAGE_INVALID_COMMAND_FORMAT = "Invalid command format: %1$s " + LS + "%2$s";
    private static final String MESSAGE_CONFIRM_DANGEROUS_OPERATION = "Confirm %1$s %2$s? (%3$s/%4$s): ";
    private static final String MESSAGE_DANGEROUS_OPERATION_CANCELLED = "%1$s operation has been cancelled.";
    private static final String MESSAGE_INVALID_CONFIRMATION_COMMAND = "Invalid confirmation command. Enter only '%1$s' or '%2$s'.";
    private static final String MESSAGE_EMPTY_HISTORY_STACK = "Unable to %1$s: You are already at the most recent %1$s state.";
    private static final String MESSAGE_INVALID_FILE = "The given file name [%1$s] is not a valid file name!";
    private static final String MESSAGE_INVALID_PROGRAM_ARGS = "Too many parameters! Correct program argument format:"
                                                            + LS + "\tjava AddressBook"
                                                            + LS + "\tjava AddressBook [custom storage file path]";
    private static final String MESSAGE_INVALID_PERSON_DISPLAYED_INDEX = "The person index provided is invalid";
    private static final String MESSAGE_INVALID_STORAGE_FILE_CONTENT = "Storage file has invalid content";
    private static final String MESSAGE_PERSON_NOT_IN_ADDRESSBOOK = "Person could not be found in address book";
    private static final String MESSAGE_ERROR_CREATING_STORAGE_FILE = "Error: unable to create file: %1$s";
    private static final String MESSAGE_ERROR_MISSING_STORAGE_FILE = "Storage file missing: %1$s";
    private static final String MESSAGE_ERROR_READING_FROM_FILE = "Unexpected error: unable to read from file: %1$s";
    private static final String MESSAGE_ERROR_WRITING_TO_FILE = "Unexpected error: unable to write to file: %1$s";
    private static final String MESSAGE_PERSONS_FOUND_OVERVIEW = "%1$d persons found!";
    private static final String MESSAGE_STORAGE_FILE_CREATED = "Created new empty storage file: %1$s";
    private static final String MESSAGE_WELCOME = "Welcome to your Address Book!";
    private static final String MESSAGE_USING_DEFAULT_FILE = "Using default storage file : " + DEFAULT_STORAGE_FILEPATH;

    // These are the prefix strings to define the data type of a command parameter
    private static final String PERSON_DATA_PREFIX_NAME = "n/";
    private static final String PERSON_DATA_PREFIX_PHONE = "p/";
    private static final String PERSON_DATA_PREFIX_EMAIL = "e/";
    private static final String COMMAND_SORT_PARAMETER_DESCENDING = "DESC";
    private static final String COMMAND_SORT_PARAMETER_ASCENDING = "ASC";
    private static final HashSet<String> POSSIBLE_SORT_ARGUMENTS =
            new HashSet<>(Arrays.asList(PERSON_DATA_PREFIX_NAME,
                                        PERSON_DATA_PREFIX_PHONE,
                                        PERSON_DATA_PREFIX_EMAIL,
                                        PERSON_DATA_PREFIX_NAME.concat(COMMAND_SORT_PARAMETER_DESCENDING),
                                        PERSON_DATA_PREFIX_PHONE.concat(COMMAND_SORT_PARAMETER_DESCENDING),
                                        PERSON_DATA_PREFIX_EMAIL.concat(COMMAND_SORT_PARAMETER_DESCENDING),
                                        PERSON_DATA_PREFIX_NAME.concat(COMMAND_SORT_PARAMETER_ASCENDING),
                                        PERSON_DATA_PREFIX_PHONE.concat(COMMAND_SORT_PARAMETER_ASCENDING),
                                        PERSON_DATA_PREFIX_EMAIL.concat(COMMAND_SORT_PARAMETER_ASCENDING)));

    private static final String PERSON_STRING_REPRESENTATION = "%1$s " // name
                                                            + PERSON_DATA_PREFIX_PHONE + "%2$s " // phone
                                                            + PERSON_DATA_PREFIX_EMAIL + "%3$s"; // email
    private static final String COMMAND_ADD_WORD = "add";
    private static final String COMMAND_ADD_DESC = "Adds a new person to the address book.";
    private static final String COMMAND_ADD_PARAMETERS = "NAME "
                                                      + PERSON_DATA_PREFIX_PHONE + "PHONE_NUMBER "
                                                      + PERSON_DATA_PREFIX_EMAIL + "EMAIL";
    private static final String COMMAND_ADD_EXAMPLE = COMMAND_ADD_WORD + " John Doe p/98765432 e/johnd@gmail.com"
                                        + LS + " Jane Doe p/+6587765552 e/janed@gmail.com";

    private static final String COMMAND_LIST_WORD = "list";
    private static final String COMMAND_LIST_DESC = "Displays all persons as a list with index numbers, "
                                        + "sorted by addition order or the specified sort order.";
    private static final String COMMAND_LIST_PARAMETER = "[optional:SORT_FIELD - '" + PERSON_DATA_PREFIX_NAME + "' "
                                        +"'" + PERSON_DATA_PREFIX_PHONE + "' "
                                        +"'" + PERSON_DATA_PREFIX_EMAIL + "' "
                                        +"'" + PERSON_DATA_PREFIX_NAME + COMMAND_SORT_PARAMETER_DESCENDING + "' "
                                        +"'" + PERSON_DATA_PREFIX_PHONE + COMMAND_SORT_PARAMETER_DESCENDING + "' "
                                        +"'" + PERSON_DATA_PREFIX_EMAIL + COMMAND_SORT_PARAMETER_DESCENDING + "']";
    private static final String COMMAND_LIST_EXAMPLE = COMMAND_LIST_WORD + LS + "\t\t\t"
                                        + COMMAND_LIST_WORD + " " + PERSON_DATA_PREFIX_NAME + LS + "\t\t\t"
                                        + COMMAND_LIST_WORD + " " + PERSON_DATA_PREFIX_NAME + " "
                                        + PERSON_DATA_PREFIX_PHONE + COMMAND_SORT_PARAMETER_DESCENDING;

    private static final String COMMAND_FIND_WORD = "find";
    private static final String COMMAND_FIND_DESC = "Finds all persons whose names contain any of the specified "
                                        + "keywords and displays them as a list with index numbers, "
                                        + "sorted by addition order or the specified sorted order.";
    private static final String COMMAND_FIND_PARAMETERS = "KEYWORD [MORE_KEYWORDS] " + COMMAND_LIST_PARAMETER;
    private static final String COMMAND_FIND_EXAMPLE = COMMAND_FIND_WORD + " alice bob charlie n/ p/DESC";

    private static final String COMMAND_EDIT_WORD = "edit";
    private static final String COMMAND_EDIT_DESC = "Edits a particular person's information. " +
            "Requires an index of a person from the most recent listing from the 'find' or 'list' command.";
    private static final String COMMAND_EDIT_PARAMETERS = "INDEX [FIELD/VALUE]";
    private static final String COMMAND_EDIT_EXAMPLE = COMMAND_EDIT_WORD + " 1 n/Ricky Ray p/12345678"
                                        + LS + "\t\t\t" + COMMAND_EDIT_WORD + " 2 e/noob@noob.com";

    private static final String COMMAND_DELETE_WORD = "delete";
    private static final String COMMAND_DELETE_DESC = "Deletes a person identified by the index number used in "
                                                    + "the last find/list call.";
    private static final String COMMAND_DELETE_PARAMETER = "INDEX";
    private static final String COMMAND_DELETE_EXAMPLE = COMMAND_DELETE_WORD + " 1";

    private static final String COMMAND_UNDO_WORD = "undo";
    private static final String COMMAND_UNDO_DESC = "Undo a previous add, edit, delete or clear operation.";
    private static final String COMMAND_UNDO_EXAMPLE = COMMAND_UNDO_WORD;

    private static final String COMMAND_REDO_WORD = "redo";
    private static final String COMMAND_REDO_DESC = "Redo a previous undo operation.";
    private static final String COMMAND_REDO_EXAMPLE = COMMAND_REDO_WORD;

    private static final String COMMAND_CLEAR_WORD = "clear";
    private static final String COMMAND_CLEAR_DESC = "Clears address book permanently.";
    private static final String COMMAND_CLEAR_EXAMPLE = COMMAND_CLEAR_WORD;

    private static final String COMMAND_HELP_WORD = "help";
    private static final String COMMAND_HELP_DESC = "Shows program usage instructions.";
    private static final String COMMAND_HELP_EXAMPLE = COMMAND_HELP_WORD;

    private static final String COMMAND_EXIT_WORD = "exit";
    private static final String COMMAND_EXIT_DESC = "Exits the program.";
    private static final String COMMAND_EXIT_EXAMPLE = COMMAND_EXIT_WORD;

    private static final String COMMAND_CONFIRM_WORD = "y";
    private static final String COMMAND_UNCONFIRM_WORD = "n";

    private static final String DIVIDER = "===================================================";


    /**
     * Offset required to convert between 1-indexing and 0-indexing.COMMAND_
     */
    private static final int DISPLAYED_INDEX_OFFSET = 1;

    /**
     * If the first non-whitespace character in a user's input line is this, that line will be ignored.
     */
    private static final char INPUT_COMMENT_MARKER = '#';

    /* Regular expression formats for input validation.
     * The patterns below are sourced online, using java.util.regex to create a pattern format for validation.
     */
    /**
     * Regex format for name formatting.
     * Custom format that allows for letters whitespaces, apostrophes, periods and hyphens.
     * Also has minimum length of 1 and max of 100.
     */
    private static final Pattern NAME_REGEX = Pattern.compile("^[a-zA-Z\\s'.,-]{1,100}$");

    /**
     * E164 phone number format adapted for java.
     * Src: http://en.wikipedia.org/wiki/E.164
     */
    private static final Pattern PHONE_E164_REGEX = Pattern.compile("^\\+?[1-9]\\d{1,14}$");

    /**
     * RFC5322 email address format adapted for java.
     * Special characters are allowed before the final @... I.e. abc$$$@def.com is a valid email
     * but are not allowed after the final @... I.e. abc@$$$.com is NOT a valid email account
     * Src: http://emailregex.com/
     */
    private static final Pattern EMAIL_RFC5322_REGEX =
            Pattern.compile("(?:[a-z0-9!#$%&'*+/=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+/=?^_`{|}~-]+)*|\"(?:[\\x01-\\x08\\x" +
                    "0b\\x0c\\x0e-\\x1f\\x21\\x23-\\x5b\\x5d-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])*\")@(?:(?" +
                    ":[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?|\\[(?:(?:25[0-5]|2[0-4][0-9" +
                    "]|[01]?[0-9][0-9]?)\\.){3}(?:25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?|[a-z0-9-]*[a-z0-9]:(?:[\\x01-" +
                    "\\x08\\x0b\\x0c\\x0e-\\x1f\\x21-\\x5a\\x53-\\x7f]|\\\\[\\x01-\\x09\\x0b\\x0c\\x0e-\\x7f])+)\\])");


    /*
     * This variable is declared for the whole class (instead of declaring it
     * inside the readUserCommand() method to facilitate automated testing using
     * the I/O redirection technique. If not, only the first line of the input
     * text file will be processed.
     */
    private static final Scanner SCANNER = new Scanner(System.in);

    /*
     * NOTE : =============================================================================================
     * Note that the type of the variable below can also be declared as List<String[]>, as follows:
     *    private static final List<String[]> ALL_PERSONS = new ArrayList<>()
     * That is because List is an interface implemented by the ArrayList class.
     * In this code we use ArrayList instead because we wanted to to stay away from advanced concepts
     * such as interface inheritance.
     * ====================================================================================================
     */

    /**
     * List of all persons in the address book.
     */
    private static final ArrayList<Person> ALL_PERSONS = new ArrayList<>();

    /**
     * List of all persons in the previous application state.
     */
    private static ArrayList<Person> saveState = new ArrayList<>();

    /**
     * Maximum size of the history state before getting deleted. Minimum value 1.
     */
    private static final int MAX_HISTORY_SIZE = 15;

    /**
     * Stack of all history states in the current session.
     */
    private static Stack<ArrayList<Person>> historyStack = new Stack<>();

    /**
     * Stack of all undo history states in the current undo sequence.
     */
    private static Stack<ArrayList<Person>> redoStack = new Stack<>();

    /**
     * Stores the most recent list of persons shown to the user as a result of a user command.
     * This is a subset of the full list. Deleting persons in the pull list does not delete
     * those persons from this list.
     */
    private static ArrayList<Person> latestPersonListingView = getAllPersonsInAddressBook(); // initial view is of all

    /**
     * The path to the file used for storing person data.
     */
    private static String storageFilePath;

    /*
     * NOTE : =============================================================
     * Notice how this method solves the whole problem at a very high level.
     * We can understand the high-level logic of the program by reading this
     * method alone.
     * If the reader wants a deeper understanding of the solution, she can go
     * to the next level of abstraction by reading the methods that are
     * referenced by the high-level method below.
     * ====================================================================
     */

    public static void main(String[] args) {
        showWelcomeMessage();
        processProgramArgs(args);
        loadDataFromStorage();
        while (true) {
            String userCommand = getUserInput(MESSAGE_PROMPT_USER_INPUT);
            echoUserCommand(userCommand);
            String feedback = executeCommand(userCommand);
            showResultToUser(feedback);
        }
    }

    /*
     * NOTE : =============================================================
     * The method header comment can be omitted if the method is trivial
     * and the header comment is going to be almost identical to the method
     * signature anyway.
     * ====================================================================
     */

    private static void showWelcomeMessage() {
        showToUser(DIVIDER, DIVIDER, VERSION, MESSAGE_WELCOME, DIVIDER);
    }

    private static void showResultToUser(String result) {
        showToUser(result, DIVIDER);
    }

    /*
     * NOTE : =============================================================
     * Parameter description can be omitted from the method header comment
     * if the parameter name is self-explanatory.
     * In the method below, '@param userInput' comment has been omitted.
     * ====================================================================
     */

    /**
     * Echoes the user input back to the user.
     */
    private static void echoUserCommand(String userCommand) {
        showToUser("[Command entered:" + userCommand + "]");
    }

    /**
     * Processes the program main method run arguments.
     * If a valid storage file is specified, sets up that file for storage.
     * Otherwise sets up the default file for storage.
     *
     * @param args full program arguments passed to application main method
     */
    private static void processProgramArgs(String[] args) {
        if (args.length >= 2) {
            showToUser(MESSAGE_INVALID_PROGRAM_ARGS);
            exitProgram();
        }

        if (args.length == 1) {
            setupGivenFileForStorage(args[0]);
        }

        if(args.length == 0) {
            setupDefaultFileForStorage();
        }
    }

    /**
     * Sets up the storage file based on the supplied file path.
     * Creates the file if it is missing.
     * Exits if the file name is not acceptable.
     */
    private static void setupGivenFileForStorage(String filePath) {

        if (!isValidFilePath(filePath)) {
            showToUser(String.format(MESSAGE_INVALID_FILE, filePath));
            exitProgram();
        }

        storageFilePath = filePath;
        createFileIfMissing(filePath);
    }

    /**
     * Displays the goodbye message and exits the runtime.
     */
    private static void exitProgram() {
        showToUser(MESSAGE_GOODBYE, DIVIDER, DIVIDER);
        System.exit(0);
    }

    /**
     * Sets up the storage based on the default file.
     * Creates file if missing.
     * Exits program if the file cannot be created.
     */
    private static void setupDefaultFileForStorage() {
        showToUser(MESSAGE_USING_DEFAULT_FILE);
        storageFilePath = DEFAULT_STORAGE_FILEPATH;
        createFileIfMissing(storageFilePath);
    }

    /**
     * Returns true if the given file path is valid.
     * A file path is valid if it has a valid parent directory as determined by {@link #hasValidParentDirectory}
     * and a valid file name as determined by {@link #hasValidFileName}.
     */
    private static boolean isValidFilePath(String filePath) {
        if (filePath == null) {
            return false;
        }
        Path filePathToValidate;
        try {
            filePathToValidate = Paths.get(filePath);
        } catch (InvalidPathException ipe) {
            return false;
        }
        return hasValidParentDirectory(filePathToValidate) && hasValidFileName(filePathToValidate);
    }

    /**
     * Returns true if the file path has a parent directory that exists.
     */
    private static boolean hasValidParentDirectory(Path filePath) {
        Path parentDirectory = filePath.getParent();
        return parentDirectory == null || Files.isDirectory(parentDirectory);
    }

    /**
     * Returns true if file path has a valid file name.
     * File name is valid if it has an extension and no reserved characters.
     * Reserved characters are OS-dependent.
     * If a file already exists, it must be a regular file.
     */
    private static boolean hasValidFileName(Path filePath) {
        return filePath.getFileName().toString().lastIndexOf('.') > 0
                && (!Files.exists(filePath) || Files.isRegularFile(filePath));
    }

    /**
     * Initialises the in-memory data using the storage file.
     * Assumption: The file exists.
     */
    private static void loadDataFromStorage() {
        initialiseAddressBookModel(loadPersonsFromFile(storageFilePath));
    }


    /*
     * ===========================================
     *           COMMAND LOGIC
     * ===========================================
     */

    /**
     * Executes the command as specified by the {@code userInputString}
     *
     * @param userInputString  raw input from user
     * @return  feedback about how the command was executed
     */
    private static String executeCommand(String userInputString) {
        final String[] commandTypeAndParams = splitCommandWordAndArgs(userInputString);
        final String commandType = commandTypeAndParams[0];
        final String commandArgs = commandTypeAndParams[1];
        switch (commandType) {
        case COMMAND_ADD_WORD:
            return executeAddPerson(commandArgs);
        case COMMAND_FIND_WORD:
            return executeFindPersons(commandArgs);
        case COMMAND_EDIT_WORD:
            return executeEditPerson(commandArgs);
        case COMMAND_LIST_WORD:
            return executeListAllPersonsInAddressBook(commandArgs);
        case COMMAND_DELETE_WORD:
            return executeDeletePerson(commandArgs);
        case COMMAND_UNDO_WORD:
            return executeUndo();
        case COMMAND_REDO_WORD:
            return executeRedo();
        case COMMAND_CLEAR_WORD:
            return executeClearAddressBook();
        case COMMAND_HELP_WORD:
            return getUsageInfoForAllCommands();
        case COMMAND_EXIT_WORD:
            executeExitProgramRequest();
        default:
            return getMessageForInvalidCommandInput(commandType, getUsageInfoForAllCommands());
        }
    }

    /**
     * Splits raw user input into command word and command arguments string
     *
     * @return  size 2 array; first element is the command type and second element is the arguments string
     */
    private static String[] splitCommandWordAndArgs(String rawUserInput) {
        final String[] split =  rawUserInput.trim().split("\\s+", 2);
        return split.length == 2 ? split : new String[] { split[0] , "" }; // else case: no parameters
    }

    /**
     * Constructs a generic error message from an unhandled application error and the source of origination.
     *
     * @param userCommand origination command of error
     * @return application error message
     */
    private static String getMessageForApplicationError(String userCommand) {
        return String.format(MESSAGE_APPLICATION_ERROR, userCommand);
    }

    /**
     * Constructs a generic feedback message for an invalid command from user, with instructions for correct usage.
     *
     * @param userCommand origination command of error
     * @param correctUsageInfo message showing the correct usage
     * @return invalid command args feedback message
     */
    private static String getMessageForInvalidCommandInput(String userCommand, String correctUsageInfo) {
        return String.format(MESSAGE_INVALID_COMMAND_FORMAT, userCommand, correctUsageInfo);
    }

    /**
     * Constructs a feedback message for empty undo or redo history.
     *
     * @param userCommand origination command of error
     * @return empty history stack feedback message
     */
    private static String getMessageForEmptyHistoryStack(String userCommand) {
        return String.format(MESSAGE_EMPTY_HISTORY_STACK, userCommand);
    }

    /**
     * Adds a person (specified by the command args) to the address book.
     * The entire command arguments string is treated as a string representation of the person to add.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeAddPerson(String commandArgs) {
        // try decoding a person from the raw args
        final Optional<Person> decodeResult = decodePersonFromString(commandArgs);

        // save state for undo
        saveStateBeforeOperation();

        // checks if args are valid (decode result will not be present if the person is invalid)
        if (decodeResult.isPresent()) {

            // add the person as specified
            final Person personToAdd = decodeResult.get();
            addPersonToAddressBook(personToAdd);

            updateStateAfterSuccessfulOperation();  // update state after successful editor operation

            return getMessageForSuccessfulAddPerson(personToAdd);

        } else {

            //invalid arg
            return getMessageForInvalidCommandInput(COMMAND_ADD_WORD, getUsageInfoForAddCommand());
        }
    }

    /**
     * Constructs a feedback message for a successful add person command execution.
     *
     * @see #executeAddPerson(String)
     * @param addedPerson person who was successfully added
     * @return successful add person feedback message
     */
    private static String getMessageForSuccessfulAddPerson(Person addedPerson) {
        return String.format(MESSAGE_ADDED, getMessageForFormattedPersonData(addedPerson));
    }

    /**
     * Finds and lists all persons in address book whose name contains any of the argument keywords.
     * Keyword matching is not case sensitive.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeFindPersons(String commandArgs) {
        final Set<String> keywords = extractKeywordsFromFindPersonArgs(commandArgs);
        final ArrayList<Person> personsFound = getPersonsWithNameContainingAnyKeyword(keywords, commandArgs);
        showToUser(personsFound);
        return getMessageForPersonsDisplayedSummary(personsFound);
    }

    /**
     * Constructs a feedback message to summarise an operation that displayed a listing of persons.
     *
     * @param personsDisplayed used to generate summary
     * @return summary message for persons displayed
     */
    private static String getMessageForPersonsDisplayedSummary(ArrayList<Person> personsDisplayed) {
        return String.format(MESSAGE_PERSONS_FOUND_OVERVIEW, personsDisplayed.size());
    }

    /**
     * Extracts keywords from the command arguments given for the find persons command.
     *
     * @param findPersonCommandArgs full command args string for the find persons command
     * @return set of keywords as specified by args
     */
    private static Set<String> extractKeywordsFromFindPersonArgs(String findPersonCommandArgs) {
        return new HashSet<>(splitByWhitespace(findPersonCommandArgs.toLowerCase().trim()));
    }

    /**
     * Retrieves all persons in the full model whose names contain some of the specified keywords.
     *
     * @param commandArgs full command args string from the find persons command
     * @param keywords for searching
     * @return sorted list of persons in full model with name containing some of the keywords
     */
    private static ArrayList<Person> getPersonsWithNameContainingAnyKeyword(Collection<String> keywords, String commandArgs) {
        final ArrayList<Person> matchedPersons = new ArrayList<>();
        for (Person person : getAllPersonsInAddressBook(extractSortParameters(commandArgs))) {
            final Set<String> wordsInName = new HashSet<>(splitByWhitespace(getNameFromPerson(person).toLowerCase()));
            if (!Collections.disjoint(wordsInName, keywords)) {
                matchedPersons.add(person);
            }
        }
        return matchedPersons;
    }

    /**
     * Edits person identified using last displayed index.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeEditPerson(String commandArgs) {
        // Check if edit arguments are valid (does not check for formatting)
        if (!isEditPersonArgsValid(commandArgs)) {
            return getMessageForInvalidCommandInput(COMMAND_EDIT_WORD, getUsageInfoForEditCommand());
        }

        // Check if edit index is valid and set a reference if successful
        final int targetVisibleIndex = extractTargetIndexFromEditPersonArgs(commandArgs);
        if (!isDisplayIndexValidForLastPersonListingView(targetVisibleIndex)) {
            return MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
        }
        final Person targetInModel = getPersonByLastVisibleIndex(targetVisibleIndex);

        // Create a new "Person" object with the entered arguments or default target's values if not available
        Person newTarget = new Person(getNameFromEditCommandArgs(commandArgs, targetInModel),
                                      getPhoneFromEditCommandArgs(commandArgs, targetInModel),
                                      getEmailFromEditCommandArgs(commandArgs, targetInModel));

        // Check if the new target's property fields are format-valid (e.g. RFC5322, E164)
        if (!isPersonValid(newTarget)) {
            return getMessageForInvalidCommandInput(COMMAND_EDIT_WORD, getUsageInfoForEditCommand());
        }

        // save state for undo
        saveStateBeforeOperation();

        // Attempt execution in the model
        if (editPersonWithinAddressBook(targetInModel, newTarget)) {

            updateStateAfterSuccessfulOperation();  // update state after successful editor operation

            return getMessageForSuccessfulEdit(targetInModel, newTarget); // success
        } else {
            return MESSAGE_PERSON_NOT_IN_ADDRESSBOOK; // not found
        }
    }

    /**
     *  Checks validity of edit person argument string's format.
     *
     * @param encoded encoded command args string for the edit person command
     * @return whether the input args string is valid
     */
    private static boolean isEditPersonArgsValid(String encoded) {
        final String matchAnyPersonDataPrefix = PERSON_DATA_PREFIX_NAME + '|'
                                              + PERSON_DATA_PREFIX_PHONE + '|'
                                              + PERSON_DATA_PREFIX_EMAIL;
        final String[] splitArgs = encoded.trim().split(matchAnyPersonDataPrefix);
        for (String arg : splitArgs) {
            if (arg.isEmpty()) return false; // non-empty arguments
        }
        return splitArgs.length <= 4 && splitArgs.length > 1; // 4 arguments or less, but minimum 2
    }

    /**
     * Extracts the target's index from the encoded edit person args string
     *
     * @param encoded encoded command args string for the delete person command
     * @return extracted index
     */
    private static int extractTargetIndexFromEditPersonArgs(String encoded) {
        // index is leading substring up to first data prefix symbol, less -1 values.
        return Integer.parseInt(encoded.split(" ")[0].trim());
    }

    /**
     * Constructs a feedback message for a successful edit person command execution.
     *
     * @see #executeEditPerson(String)
     * @param oldPerson old person that was removed
     * @param newPerson the new person that was added
     * @return successful edit person feedback message
     */
    private static String getMessageForSuccessfulEdit(Person oldPerson, Person newPerson) {
        return String.format(MESSAGE_EDIT_PERSON_SUCCESS,
                                getMessageForFormattedPersonData(oldPerson),
                                getMessageForFormattedPersonData(newPerson));
    }

    /**
     * Extracts substring representing person name from person string representation
     *
     * @param encoded encoded command args string for the edit person command
     * @param targetPerson Person instance of which email should be used as a fallback if no name is to be changed.
     * @return name argument
     */
    private static String getNameFromEditCommandArgs(String encoded, Person targetPerson) {
        final int indexOfNamePrefix = encoded.indexOf(PERSON_DATA_PREFIX_NAME);

        // if no such occurrence, return original value.
        if (indexOfNamePrefix == -1) return getNameFromPerson(targetPerson);

        // calculate string from encoded data
        return removeFirstPrefixSign(
                encoded.substring(indexOfNamePrefix, getNextEditArgIndex(encoded, indexOfNamePrefix)).trim(),
                PERSON_DATA_PREFIX_NAME);
    }

    /**
     * Extracts substring representing person phone number from person string representation
     *
     * @param encoded encoded command args string for the edit person command
     * @param targetPerson Person instance of which email should be used as a fallback if no phone number is to be changed.
     * @return phone argument
     */
    private static String getPhoneFromEditCommandArgs(String encoded, Person targetPerson) {
        final int indexOfPhonePrefix = encoded.indexOf(PERSON_DATA_PREFIX_PHONE);

        // if no such occurrence, return original value.
        if (indexOfPhonePrefix == -1) return getPhoneFromPerson(targetPerson);

        // calculate string from encoded data
        return removeFirstPrefixSign(
                encoded.substring(indexOfPhonePrefix, getNextEditArgIndex(encoded, indexOfPhonePrefix)).trim(),
                PERSON_DATA_PREFIX_PHONE);
    }

    /**
     * Extracts substring representing person email from person string representation
     *
     * @param encoded encoded command args string for the edit person command
     * @param targetPerson Person instance of which email should be used as a fallback if no email is to be changed.
     * @return email argument
     */
    private static String getEmailFromEditCommandArgs(String encoded, Person targetPerson) {
        final int indexOfEmailPrefix = encoded.indexOf(PERSON_DATA_PREFIX_EMAIL);

        // if no such occurrence, return original value.
        if (indexOfEmailPrefix == -1) return getEmailFromPerson(targetPerson);

        // calculate string from encoded data
        return removeFirstPrefixSign(
                encoded.substring(indexOfEmailPrefix, getNextEditArgIndex(encoded, indexOfEmailPrefix)).trim(),
                PERSON_DATA_PREFIX_EMAIL);
    }

    /**
     * Low level utility method to assist computation of substring positions of arguments.
     *
     * @param encoded encoded command args string for the edit person command
     * @param indexOfArg index of argument position within the encoding.
     * @return the index of the next argument within the encoded string,
     *          the end of the string if it's the last argument or,
     *          -1 if no index exists for the specified argument within the encoded string.
     */
    private static int getNextEditArgIndex(String encoded, int indexOfArg) {
        final int indexOfNamePrefix = encoded.indexOf(PERSON_DATA_PREFIX_NAME);
        final int indexOfPhonePrefix = encoded.indexOf(PERSON_DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(PERSON_DATA_PREFIX_EMAIL);
        int[] indexArray = new int[] { indexOfNamePrefix, indexOfPhonePrefix, indexOfEmailPrefix };
        Arrays.sort(indexArray);
        int foundIndex = Arrays.binarySearch(indexArray, indexOfArg);

        //argument index is not found within the search array.
        if (foundIndex == -1) {
            return foundIndex;

            // found index is last argument, target is from own prefix to end of string
        } else if (foundIndex == indexArray.length - 1) {
            return encoded.length();

            // found index is somewhere in the middle, target is from own prefix to next prefix
        } else {
            return indexArray[foundIndex + 1];
        }
    }

    /**
     * Deletes person identified using last displayed index.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeDeletePerson(String commandArgs) {

        // Check if edit arguments are valid (does not check for formatting)
        if (!isDeletePersonArgsValid(commandArgs)) {
            return getMessageForInvalidCommandInput(COMMAND_DELETE_WORD, getUsageInfoForDeleteCommand());
        }

        // Check if edit index is valid and set a reference if successful
        final int targetVisibleIndex = extractTargetIndexFromDeletePersonArgs(commandArgs);
        if (!isDisplayIndexValidForLastPersonListingView(targetVisibleIndex)) {
            return MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
        }
        final Person targetInModel = getPersonByLastVisibleIndex(targetVisibleIndex);
        if (!isPersonValid(targetInModel)) {
            return MESSAGE_PERSON_NOT_IN_ADDRESSBOOK;
        }

        // Prompt user to confirm delete
        if (isDangerousOperationConfirmed(getMessageForConfirmDeletePerson(targetInModel))) {
            // save state for undo
            saveStateBeforeOperation();
        } else {
            return getMessageForCancelledDangerousOperation(COMMAND_DELETE_WORD);
        }

        // Attempt execution in the model
        if (deletePersonFromAddressBook(targetInModel)) {

            updateStateAfterSuccessfulOperation();  // update state after successful editor operation

            return getMessageForSuccessfulDelete(targetInModel); // success
        } else {
            return MESSAGE_PERSON_NOT_IN_ADDRESSBOOK; // not found
        }
    }

    /**
     * Checks validity of delete person argument string's format.
     *
     * @param rawArgs raw command args string for the delete person command
     * @return whether the input args string is valid
     */
    private static boolean isDeletePersonArgsValid(String rawArgs) {
        try {
            final int extractedIndex = Integer.parseInt(rawArgs.trim()); // use standard libraries to parse
            return extractedIndex >= DISPLAYED_INDEX_OFFSET;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    /**
     * Extracts the target's index from the raw delete person args string
     *
     * @param rawArgs raw command args string for the delete person command
     * @return extracted index
     */
    private static int extractTargetIndexFromDeletePersonArgs(String rawArgs) {
        return Integer.parseInt(rawArgs.trim());
    }

    /**
     * Checks that the given index is within bounds and valid for the last shown person list view.
     *
     * @param index to check
     * @return whether it is valid
     */
    private static boolean isDisplayIndexValidForLastPersonListingView(int index) {
        return index >= DISPLAYED_INDEX_OFFSET && index < latestPersonListingView.size() + DISPLAYED_INDEX_OFFSET;
    }

    /**
     * Constructs a message to prompt the user before a delete operation.
     *
     * @param personToDelete the person to be deleted.
     * @return a string containing the message to prompt the user.
     */
    private static String getMessageForConfirmDeletePerson(Person personToDelete) {
        return String.format(MESSAGE_CONFIRM_DANGEROUS_OPERATION,
                COMMAND_DELETE_WORD,
                getMessageForFormattedPersonData(personToDelete),
                COMMAND_CONFIRM_WORD,
                COMMAND_UNCONFIRM_WORD);
    }

    /**
     * Constructs a feedback message for a successful delete person command execution.
     *
     * @see #executeDeletePerson(String)
     * @param deletedPerson successfully deleted
     * @return successful delete person feedback message
     */
    private static String getMessageForSuccessfulDelete(Person deletedPerson) {
        return String.format(MESSAGE_DELETE_PERSON_SUCCESS, getMessageForFormattedPersonData(deletedPerson));
    }

    /**
     * Gets user input for a dangerous operation and returns true if user has allowed the operation to proceed.
     *
     * @param promptString the message to prompt the user with.
     * @return true if user entered the confirm command or false if user entered the un-confirm command,
     * otherwise recursively call the function for a valid input.
     */
    private static boolean isDangerousOperationConfirmed(String promptString) {
        String userInput = getUserInput(promptString);
        echoUserCommand(userInput);
        String[] commandTypeAndParams = splitCommandWordAndArgs(userInput);
        String commandType = commandTypeAndParams[0];
        if (commandType.toLowerCase().equals(COMMAND_CONFIRM_WORD)
         || commandType.toLowerCase().equals(COMMAND_UNCONFIRM_WORD)) {
            return commandType.toLowerCase().equals(COMMAND_CONFIRM_WORD);
        } else {
            showToUser(String.format(MESSAGE_INVALID_CONFIRMATION_COMMAND, COMMAND_CONFIRM_WORD, COMMAND_UNCONFIRM_WORD));
            return isDangerousOperationConfirmed(promptString);
        }
    }

    /**
     * Constructs a message to inform that the user has cancelled a dangerous operation.
     *
     * @param userCommand the command word for the dangerous operation.
     * @return a formatted string showing the cancelled operation.
     */
    private static String getMessageForCancelledDangerousOperation(String userCommand) {
        return String.format(MESSAGE_DANGEROUS_OPERATION_CANCELLED, userCommand);
    }

    /**
     * Constructs a message to prompt the user before a clear operation.
     *
     * @return a string containing the message to prompt the user.
     */
    private static String getMessageForConfirmClearAddressbook() {
        return String.format(MESSAGE_CONFIRM_DANGEROUS_OPERATION,
                COMMAND_CLEAR_WORD,
                storageFilePath,
                COMMAND_CONFIRM_WORD,
                COMMAND_UNCONFIRM_WORD);
    }

    /**
     * Clears all persons in the address book.
     *
     * @return feedback display message for the operation result
     */
    private static String executeClearAddressBook() {
        // Prompt user to confirm clear address book
        if (isDangerousOperationConfirmed(getMessageForConfirmClearAddressbook())) {
            // save state for undo
            saveStateBeforeOperation();

            clearAddressBook(); // clear

            // update state after successful editor operation
            updateStateAfterSuccessfulOperation();

            return MESSAGE_ADDRESSBOOK_CLEARED;
        } else {
            return getMessageForCancelledDangerousOperation(COMMAND_CLEAR_WORD);
        }
    }

    /**
     * Undoes the last add, edit, delete or clear operation
     *
     * @return feedback display message for the operation result
     */
    private static String executeUndo() {
        if (historyStack.isEmpty()) {
            return getMessageForEmptyHistoryStack(COMMAND_UNDO_WORD);
        }
        saveStateBeforeOperation();
        if (!loadAddressbookState(historyStack.pop())) {
            return getMessageForApplicationError(COMMAND_UNDO_WORD);
        }
        addRedoStateAfterSuccessfulUndo();
        updateLatestViewedPersonListing(getAllPersonsInAddressBook());
        return getMessageForSuccessfulUndoRedo(saveState, COMMAND_UNDO_WORD);
    }

    /**
     * Redoes the previous undo operation. Will fail if redo stack is empty.
     *
     * @return feedback display message for the operation result
     */
    private static String executeRedo() {
        if (redoStack.isEmpty()) {
            return getMessageForEmptyHistoryStack(COMMAND_REDO_WORD);
        }
        saveStateBeforeOperation();
        if (!loadAddressbookState(redoStack.pop())) {
            return getMessageForApplicationError(COMMAND_REDO_WORD);
        }
        addStateAfterSuccessfulOperation();
        updateLatestViewedPersonListing(getAllPersonsInAddressBook());
        return getMessageForSuccessfulUndoRedo(saveState, COMMAND_REDO_WORD);
    }

    /**
     * Logic for saving state before all editor operations (add,edit,delete,clear).
     * Needs to be called before the beginning of execution.
     */
    private static void saveStateBeforeOperation() {
        saveState = new ArrayList<>(getAllPersonsInAddressBook());
    }

    /**
     * Adds the latest saveState to the undo stack after a successful editor operation (add,edit,delete,clear),
     * up to the maximum history size.
     * Needs to be called after successful execution of an editor operation.
     */
    private static void addStateAfterSuccessfulOperation() {
        while (historyStack.size() >= MAX_HISTORY_SIZE) historyStack.removeElementAt(0);
        historyStack.push(saveState);
    }

    /**
     * Adds the latest saveState to the redo stack after a successful undo operation.
     * This will automatically match the maximum history size so no additional check is necessary.
     * Needs to be called after successful execution of an undo operation.
     */
    private static void addRedoStateAfterSuccessfulUndo() {
        redoStack.push(saveState);
    }

    /**
     * Clears the redo stack after successful editor operation (add,edit,delete,clear).
     * New 'branch' of edits will override previous redo stack.
     */
    private static void clearRedoStackAfterSuccessfulOperation() {
        redoStack.clear();
    }

    /**
     * Adds the latest saveState to the undo stack after a successful editor operation then clears the redo stack.
     * Not to be used with redo operations.
     */
    private static void updateStateAfterSuccessfulOperation() {
        addStateAfterSuccessfulOperation(); // update state for undo
        clearRedoStackAfterSuccessfulOperation(); // clear redo for new redo branch
    }

    /**
     * Constructs a feedback message for a successful undo or redo command execution.
     *
     * @see #executeUndo()
     * @see #executeRedo()
     * @param previousState the address book of the previous state
     * @param userCommand the type of operation (undo or redo)
     * @return successful undo or redo feedback message
     */
    private static String getMessageForSuccessfulUndoRedo(ArrayList<Person> previousState, String userCommand) {
        StringBuilder successfulMessageBuilder = new StringBuilder(String.format(MESSAGE_UNDO_REDO_SUCCESS, userCommand));
        // show added elements not existing in previous
        for (Person person : getAllPersonsInAddressBook()) {
            if (!previousState.contains(person)) {
                successfulMessageBuilder.append(LS);
                successfulMessageBuilder.append(getMessageForSuccessfulAddPerson(person));
            }
        }
        // show elements in previous not existing in current
        for (Person person : previousState) {
            if (!getAllPersonsInAddressBook().contains(person)) {
                successfulMessageBuilder.append(LS);
                successfulMessageBuilder.append(getMessageForSuccessfulDelete(person));
            }
        }
        return successfulMessageBuilder.toString();
    }

    /**
     * Displays all persons in the address book to the user; in added order, or sorted order.
     *
     * @param commandArgs full command args string from the user
     * @return feedback display message for the operation result
     */
    private static String executeListAllPersonsInAddressBook(String commandArgs) {
        ArrayList<Person> toBeDisplayed = getAllPersonsInAddressBook(commandArgs);
        showToUser(toBeDisplayed);
        return getMessageForPersonsDisplayedSummary(toBeDisplayed);
    }

    /**
     * Requests to terminate the program.
     */
    private static void executeExitProgramRequest() {
        exitProgram();
    }

    /*
     * ===========================================
     *               UI LOGIC
     * ===========================================
     */

    /**
     * Prompts for the command and reads the text entered by the user.
     * Ignores lines with first non-whitespace char equal to {@link #INPUT_COMMENT_MARKER} (considered comments)
     *
     * @return full line entered by the user
     */
    private static String getUserInput(String promptText) {
        System.out.print(LINE_PREFIX + promptText);
        String inputLine = SCANNER.nextLine();
        // silently consume all blank and comment lines
        while (inputLine.trim().isEmpty() || inputLine.trim().charAt(0) == INPUT_COMMENT_MARKER) {
            inputLine = SCANNER.nextLine();
        }
        return inputLine;
    }

   /*
    * NOTE : =============================================================
    * Note how the method below uses Java 'Varargs' feature so that the
    * method can accept a varying number of message parameters.
    * ====================================================================
    */

    /**
     * Shows a message to the user
     */
    private static void showToUser(String... message) {
        for (String m : message) {
            System.out.println(LINE_PREFIX + m);
        }
    }

    /**
     * Shows the list of persons to the user.
     * The list will be indexed, starting from 1.
     *
     */
    private static void showToUser(ArrayList<Person> persons) {
        String listAsString = getDisplayString(persons);
        showToUser(listAsString);
        updateLatestViewedPersonListing(persons);
    }

    /**
     * Returns the display string representation of the list of persons.
     */
    private static String getDisplayString(ArrayList<Person> persons) {
        final StringBuilder messageAccumulator = new StringBuilder();
        for (int i = 0; i < persons.size(); i++) {
            final Person person = persons.get(i);
            final int displayIndex = i + DISPLAYED_INDEX_OFFSET;
            messageAccumulator.append('\t')
                              .append(getIndexedPersonListElementMessage(displayIndex, person))
                              .append(LS);
        }
        return messageAccumulator.toString();
    }

    /**
     * Constructs a prettified listing element message to represent a person and their data.
     *
     * @param visibleIndex visible index for this listing
     * @param person to show
     * @return formatted listing message with index
     */
    private static String getIndexedPersonListElementMessage(int visibleIndex, Person person) {
        return String.format(MESSAGE_DISPLAY_LIST_ELEMENT_INDEX, visibleIndex) + getMessageForFormattedPersonData(person);
    }

    /**
     * Constructs a prettified string to show the user a person's data.
     *
     * @param person to show
     * @return formatted message showing internal state
     */
    private static String getMessageForFormattedPersonData(Person person) {
        return String.format(MESSAGE_DISPLAY_PERSON_DATA,
                getNameFromPerson(person), getPhoneFromPerson(person), getEmailFromPerson(person));
    }

    /**
     * Updates the latest person listing view the user has seen.
     *
     * @param newListing the new listing of persons
     */
    private static void updateLatestViewedPersonListing(ArrayList<Person> newListing) {
        // clone to insulate from future changes to arg list
        latestPersonListingView = new ArrayList<>(newListing);
    }

    /**
     * Retrieves the person identified by the displayed index from the last shown listing of persons.
     *
     * @param lastVisibleIndex displayed index from last shown person listing
     * @return the actual person object in the last shown person listing
     */
    private static Person getPersonByLastVisibleIndex(int lastVisibleIndex) {
       return latestPersonListingView.get(lastVisibleIndex - DISPLAYED_INDEX_OFFSET);
    }


    /*
     * ===========================================
     *             STORAGE LOGIC
     * ===========================================
     */

    /**
     * Creates storage file if it does not exist. Shows feedback to user.
     *
     * @param filePath file to create if not present
     */
    private static void createFileIfMissing(String filePath) {
        final File storageFile = new File(filePath);
        if (storageFile.exists()) {
            return;
        }

        showToUser(String.format(MESSAGE_ERROR_MISSING_STORAGE_FILE, filePath));

        try {
            storageFile.createNewFile();
            showToUser(String.format(MESSAGE_STORAGE_FILE_CREATED, filePath));
        } catch (IOException ioe) {
            showToUser(String.format(MESSAGE_ERROR_CREATING_STORAGE_FILE, filePath));
            exitProgram();
        }
    }

    /**
     * Converts contents of a file into a list of persons.
     * Shows error messages and exits program if any errors in reading or decoding was encountered.
     *
     * @param filePath file to load from
     * @return the list of decoded persons
     */
    private static ArrayList<Person> loadPersonsFromFile(String filePath) {
        final Optional<ArrayList<Person>> successfullyDecoded = decodePersonsFromStrings(getLinesInFile(filePath));
        if (!successfullyDecoded.isPresent()) {
            showToUser(MESSAGE_INVALID_STORAGE_FILE_CONTENT);
            exitProgram();
        }
        return successfullyDecoded.get();
    }

    /**
     * Gets all lines in the specified file as a list of strings. Line separators are removed.
     * Shows error messages and exits program if unable to read from file.
     */
    private static ArrayList<String> getLinesInFile(String filePath) {
        ArrayList<String> lines = null;
        try {
            lines = new ArrayList<>(Files.readAllLines(Paths.get(filePath)));
        } catch (FileNotFoundException fnfe) {
            showToUser(String.format(MESSAGE_ERROR_MISSING_STORAGE_FILE, filePath));
            exitProgram();
        } catch (IOException ioe) {
            showToUser(String.format(MESSAGE_ERROR_READING_FROM_FILE, filePath));
            exitProgram();
        }
        return lines;
    }

    /**
     * Saves all data to the file. Exits program if there is an error saving to file.
     *
     * @param filePath file for saving
     */
    private static void savePersonsToFile(ArrayList<Person> persons, String filePath) {
        final ArrayList<String> linesToWrite = encodePersonsToStrings(persons);
        try {
            Files.write(Paths.get(storageFilePath), linesToWrite);
        } catch (IOException ioe) {
            showToUser(String.format(MESSAGE_ERROR_WRITING_TO_FILE, filePath));
            exitProgram();
        }
    }


    /*
     * ================================================================================
     *        INTERNAL ADDRESS BOOK DATA METHODS
     * ================================================================================
     */

    /**
     * Adds a person to the address book. Saves changes to storage file.
     *
     * @param person to add
     */
    private static void addPersonToAddressBook(Person person) {
        ALL_PERSONS.add(person);
        savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
    }

    /**
     * Replaces the specified person in the addressbook with a new person
     *
     * @param exactPerson the actual person inside the address book (exactPerson == the person to delete in the full list)
     * @param newPerson the new person to be replaced with inside the addressbook.
     * @return true if the given person was found and replaced in the model
     */
    private static boolean editPersonWithinAddressBook(Person exactPerson, Person newPerson) {
        final boolean changed =
                (latestPersonListingView.set(latestPersonListingView.indexOf(exactPerson), newPerson).equals(exactPerson))
                && (ALL_PERSONS.set(ALL_PERSONS.indexOf(exactPerson), newPerson).equals(exactPerson));
        if (changed) {
            savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
        }
        return changed;
    }

    /**
     * Deletes the specified person from the addressbook if it is inside. Saves any changes to storage file.
     *
     * @param exactPerson the actual person inside the address book (exactPerson == the person to delete in the full list)
     * @return true if the given person was found and deleted in the model
     */
    private static boolean deletePersonFromAddressBook(Person exactPerson) {
        final boolean changed = ALL_PERSONS.remove(exactPerson)
                && (latestPersonListingView.set(latestPersonListingView.indexOf(exactPerson), null) == exactPerson);
        if (changed) {
            savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
        }
        return changed;
    }

    /**
     * Replaces the current addressbook state with a new addressbook state
     *
     * @param addressbookState new state to be loaded
     * @return true if successfully loaded state into database and false otherwise.
     */
    private static boolean loadAddressbookState(ArrayList<Person> addressbookState) {
        ALL_PERSONS.clear();
        //if new state is empty, simply return true as new state would be the same as the cleared list.
        final boolean changed = addressbookState.isEmpty() || ALL_PERSONS.addAll(addressbookState);
        // successfully loaded specified state to current state
        if (changed) {
            savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);

            // failed to load state: revert to previous save state
        } else {
            ALL_PERSONS.clear();
            ALL_PERSONS.addAll(saveState);
        }
        return changed;
    }

    /**
     * @return all persons in the address book
     */
    private static ArrayList<Person> getAllPersonsInAddressBook() {
        return ALL_PERSONS;
    }

    //TODO: parse parameter implmentation
    /**
     *
     * @param rawArgs raw command args string for the delete person command
     * @return all persons in the address book, using the sorted arguments
     */
    private static ArrayList<Person> getAllPersonsInAddressBook(String rawArgs) {
        if (rawArgs.matches("")) return getAllPersonsInAddressBook();
        return getSortedPersonsInAddressBook(getAllPersonsInAddressBook(), rawArgs.split(" "));
    }

    /**
     * Generates a sorted list of persons using a specified list of persons and string array of sort arguments
     *
     * @param persons an unordered or pre-ordered ArrayList of persons
     * @param sortArgs the arguments in which to be sorted, in order of priority (0 - highest)
     * @return a sorted list of persons
     */
    private static ArrayList<Person> getSortedPersonsInAddressBook(ArrayList<Person> persons, String[] sortArgs) {
        ArrayList<Person> sortedPersons = new ArrayList<>(persons);

        // If there is a sort argument that is not defined, return an empty result set.
        for (String argument : sortArgs) {
            if (!isValidSortArgument(argument)) {
                showToUser(getMessageForInvalidCommandInput(COMMAND_LIST_WORD, getUsageInfoForViewCommand()));
                return new ArrayList<>();
            }
        }

        // Using a multi-argument comparator, we implement sort by x then by y then by z... efficiently.
        sortedPersons.sort((person1, person2) -> {
            int c = 0;
            for (String sortArgument : sortArgs) {
                if (c == 0) {
                    c = sortArgumentCompareValue(sortArgument, person1, person2);
                }
            }
            return c;
        });

        return sortedPersons;
    }

    /**
     * Checks if a given sort argument string is a valid sort argument.
     *
     * @see #POSSIBLE_SORT_ARGUMENTS
     * @param sortArgument the argument prefix string to be checked.
     * @return true if argument is valid, false otherwise.
     */
    private static boolean isValidSortArgument(String sortArgument) {
        return POSSIBLE_SORT_ARGUMENTS.contains(sortArgument);
    }

    /**
     * Executes and extends compareTo for two Person objects given a particular sort argument string.
     *
     * @param sortArgument string of sort argument prefix.
     * @param person1 first person to be compared to.
     * @param person2 other person to be compared to.
     * @return object.compareTo(other) value for the two persons or 0 if sort argument is invalid.
     */
    private static int sortArgumentCompareValue(String sortArgument, Person person1, Person person2) {
        switch (sortArgument) {
            case PERSON_DATA_PREFIX_NAME:
                return getNameFromPerson(person1).compareTo(getNameFromPerson(person2));
            case PERSON_DATA_PREFIX_PHONE:
                return getPhoneFromPerson(person1).compareTo(getPhoneFromPerson(person2));
            case PERSON_DATA_PREFIX_EMAIL:
                return getEmailFromPerson(person1).compareTo(getEmailFromPerson(person2));
            case PERSON_DATA_PREFIX_NAME + COMMAND_SORT_PARAMETER_DESCENDING:
                return getNameFromPerson(person2).compareTo(getNameFromPerson(person1));
            case PERSON_DATA_PREFIX_PHONE + COMMAND_SORT_PARAMETER_DESCENDING:
                return getPhoneFromPerson(person2).compareTo(getPhoneFromPerson(person1));
            case PERSON_DATA_PREFIX_EMAIL + COMMAND_SORT_PARAMETER_DESCENDING:
                return getEmailFromPerson(person2).compareTo(getEmailFromPerson(person1));
            case PERSON_DATA_PREFIX_NAME + COMMAND_SORT_PARAMETER_ASCENDING:
                return getNameFromPerson(person1).compareTo(getNameFromPerson(person2));
            case PERSON_DATA_PREFIX_PHONE + COMMAND_SORT_PARAMETER_ASCENDING:
                return getPhoneFromPerson(person1).compareTo(getPhoneFromPerson(person2));
            case PERSON_DATA_PREFIX_EMAIL + COMMAND_SORT_PARAMETER_ASCENDING:
                return getEmailFromPerson(person1).compareTo(getEmailFromPerson(person2));
            default:
                showToUser(getMessageForInvalidCommandInput(COMMAND_LIST_WORD, getUsageInfoForViewCommand()));
                return 0;
        }
    }

    /**
     * Clears all persons in the address book and saves changes to file.
     */
    private static void clearAddressBook() {
        ALL_PERSONS.clear();
        savePersonsToFile(getAllPersonsInAddressBook(), storageFilePath);
    }

    /**
     * Resets the internal model with the given data. Does not save to file.
     *
     * @param persons list of persons to initialise the model with
     */
    private static void initialiseAddressBookModel(ArrayList<Person> persons) {
        ALL_PERSONS.clear();
        ALL_PERSONS.addAll(persons);
    }


    /*
     * ===========================================
     *             PERSON METHODS
     * ===========================================
     */

    /**
     * Returns the given person's name
     *
     * @param person whose name you want
     */
    private static String getNameFromPerson(Person person) {
        return person.getName();
    }

    /**
     * Returns given person's phone number
     *
     * @param person whose phone number you want
     */
    private static String getPhoneFromPerson(Person person) {
        return person.getPhoneNumber();
    }

    /**
     * Returns given person's email
     *
     * @param person whose email you want
     */
    private static String getEmailFromPerson(Person person) {
        return person.getEmail();
    }

    /**
     * Creates a person from the given data.
     *
     * @param name of person
     * @param phone without data prefix
     * @param email without data prefix
     * @return constructed person
     */
    private static Person makePersonFromData(String name, String phone, String email) {
        return new Person(name,phone,email);
    }

    /**
     * Encodes a person into a decodable and readable string representation.
     *
     * @param person to be encoded
     * @return encoded string
     */
    private static String encodePersonToString(Person person) {
        return String.format(PERSON_STRING_REPRESENTATION,
                getNameFromPerson(person), getPhoneFromPerson(person), getEmailFromPerson(person));
    }

    /**
     * Encodes list of persons into list of decodable and readable string representations.
     *
     * @param persons to be encoded
     * @return encoded strings
     */
    private static ArrayList<String> encodePersonsToStrings(ArrayList<Person> persons) {
        final ArrayList<String> encoded = new ArrayList<>();
        for (Person person : persons) {
            encoded.add(encodePersonToString(person));
        }
        return encoded;
    }

    /*
     * NOTE : =============================================================
     * Note the use of Java's new 'Optional' feature to indicate that
     * the return value may not always be present.
     * ====================================================================
     */

    /**
     * Decodes a person from it's supposed string representation.
     *
     * @param encoded string to be decoded
     * @return if cannot decode: empty Optional
     *         else: Optional containing decoded person
     */
    private static Optional<Person> decodePersonFromString(String encoded) {
        // check that we can extract the parts of a person from the encoded string
        if (!isPersonDataExtractableFrom(encoded)) {
            return Optional.empty();
        }
        final Person decodedPerson = makePersonFromData(
                extractNameFromPersonString(encoded),
                extractPhoneFromPersonString(encoded),
                extractEmailFromPersonString(encoded)
        );
        // check that the constructed person is valid
        return isPersonValid(decodedPerson) ? Optional.of(decodedPerson) : Optional.empty();
    }

    /**
     * Decodes persons from a list of string representations.
     *
     * @param encodedPersons strings to be decoded
     * @return if cannot decode any: empty Optional
     *         else: Optional containing decoded persons
     */
    private static Optional<ArrayList<Person>> decodePersonsFromStrings(ArrayList<String> encodedPersons) {
        final ArrayList<Person> decodedPersons = new ArrayList<>();
        for (String encodedPerson : encodedPersons) {
            final Optional<Person> decodedPerson = decodePersonFromString(encodedPerson);
            if (!decodedPerson.isPresent()) {
                return Optional.empty();
            }
            decodedPersons.add(decodedPerson.get());
        }
        return Optional.of(decodedPersons);
    }

    /**
     * Returns true if person data (email, name, phone etc) can be extracted from the argument string.
     * Format is [name] p/[phone] e/[email], phone and email positions can be swapped.
     *
     * @param personData person string representation
     */
    private static boolean isPersonDataExtractableFrom(String personData) {
        final String matchAnyPersonDataPrefix = PERSON_DATA_PREFIX_PHONE + '|' + PERSON_DATA_PREFIX_EMAIL;
        final String[] splitArgs = personData.trim().split(matchAnyPersonDataPrefix);
        return splitArgs.length == 3 // 3 arguments
                && !splitArgs[0].isEmpty() // non-empty arguments
                && !splitArgs[1].isEmpty()
                && !splitArgs[2].isEmpty();
    }

    /**
     * Extracts substring representing person name from person string representation
     *
     * @param encoded person string representation
     * @return name argument
     */
    private static String extractNameFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(PERSON_DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(PERSON_DATA_PREFIX_EMAIL);
        // name is leading substring up to first data prefix symbol
        int indexOfFirstPrefix = Math.min(indexOfEmailPrefix, indexOfPhonePrefix);
        return encoded.substring(0, indexOfFirstPrefix).trim();
    }

    /**
     * Extracts substring representing sort parameters from search parameter substring.
     *
     * @param findPersonCommandArgs full command args string for the find persons command
     * @return sort arguments substring
     */
    private static String extractSortParameters(String findPersonCommandArgs) {
        //TODO extract generic parameter implementation
        String[] splitWords = findPersonCommandArgs.split(" ");
        StringBuilder sortParameterStringBuilder = new StringBuilder();
        for (String word : splitWords) {
            if (isValidSortArgument(word)) {
                sortParameterStringBuilder.append(word);
                sortParameterStringBuilder.append(" ");
            }
        }

        return sortParameterStringBuilder.toString();
    }

    /**
     * Extracts substring representing phone number from person string representation
     *
     * @param encoded person string representation
     * @return phone number argument WITHOUT prefix
     */
    private static String extractPhoneFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(PERSON_DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(PERSON_DATA_PREFIX_EMAIL);

        // phone is last arg, target is from prefix to end of string
        if (indexOfPhonePrefix > indexOfEmailPrefix) {
            return removeFirstPrefixSign(encoded.substring(indexOfPhonePrefix, encoded.length()).trim(),
                    PERSON_DATA_PREFIX_PHONE);

        // phone is middle arg, target is from own prefix to next prefix
        } else {
            return removeFirstPrefixSign(
                    encoded.substring(indexOfPhonePrefix, indexOfEmailPrefix).trim(),
                    PERSON_DATA_PREFIX_PHONE);
        }
    }

    /**
     * Extracts substring representing email from person string representation
     *
     * @param encoded person string representation
     * @return email argument WITHOUT prefix
     */
    private static String extractEmailFromPersonString(String encoded) {
        final int indexOfPhonePrefix = encoded.indexOf(PERSON_DATA_PREFIX_PHONE);
        final int indexOfEmailPrefix = encoded.indexOf(PERSON_DATA_PREFIX_EMAIL);

        // email is last arg, target is from prefix to end of string
        if (indexOfEmailPrefix > indexOfPhonePrefix) {
            return removeFirstPrefixSign(encoded.substring(indexOfEmailPrefix, encoded.length()).trim(),
                    PERSON_DATA_PREFIX_EMAIL);

        // email is middle arg, target is from own prefix to next prefix
        } else {
            return removeFirstPrefixSign(
                    encoded.substring(indexOfEmailPrefix, indexOfPhonePrefix).trim(),
                    PERSON_DATA_PREFIX_EMAIL);
        }
    }

    /**
     * Returns true if the given person is valid.
     *
     * @param person Person object representation (used in internal data)
     */
    private static boolean isPersonValid(Person person) {
        return person != null
                && isPersonNameValid(getNameFromPerson(person))
                && isPersonPhoneValid(getPhoneFromPerson(person))
                && isPersonEmailValid(getEmailFromPerson(person));
    }

    /*
     * NOTE : =============================================================
     * Note the use of 'regular expressions' in the method below.
     * Regular expressions can be very useful in checking if a a string
     * follows a specific format.
     * ====================================================================
     */

    /**
     * Returns true if the given string as a legal person name
     *
     * @param name to be validated
     */
    private static boolean isPersonNameValid(String name) {
        return NAME_REGEX.matcher(name).matches(); //name matches custom name regex validation.
    }

    /**
     * Returns true if the given string as a legal person phone number
     *
     * @param phone to be validated
     */
    private static boolean isPersonPhoneValid(String phone) {
        return PHONE_E164_REGEX.matcher(phone).matches();  //phone is compliant with E164 international number standards.
    }

    /**
     * Returns true if the given string is a legal person email
     *
     * @param email to be validated
     * @return whether arg is a valid person email
     */
    private static boolean isPersonEmailValid(String email) {
        return EMAIL_RFC5322_REGEX.matcher(email).matches();   //email is compliant with RFC5322 standard.
    }

    /*
     * ===============================================
     *         COMMAND HELP INFO FOR USERS
     * ===============================================
     */

    /** Returns usage info for all commands */
    private static String getUsageInfoForAllCommands() {
        return LS + getUsageInfoForAddCommand() + LS
                + getUsageInfoForFindCommand() + LS
                + getUsageInfoForViewCommand() + LS
                + getUsageInfoForEditCommand() + LS
                + getUsageInfoForDeleteCommand() + LS
                + getUsageInfoForUndoCommand() + LS
                + getUsageInfoForRedoCommand() + LS
                + getUsageInfoForClearCommand() + LS
                + getUsageInfoForExitCommand() + LS
                + getUsageInfoForHelpCommand();
    }

    /** Returns the string for showing 'add' command usage instruction */
    private static String getUsageInfoForAddCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_ADD_WORD, COMMAND_ADD_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_ADD_PARAMETERS) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_ADD_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'find' command usage instruction */
    private static String getUsageInfoForFindCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_FIND_WORD, COMMAND_FIND_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_FIND_PARAMETERS) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_FIND_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'delete' command usage instruction */
    private static String getUsageInfoForDeleteCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_DELETE_WORD, COMMAND_DELETE_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_DELETE_PARAMETER) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_DELETE_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'undo' command usage instruction */
    private static String getUsageInfoForUndoCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_UNDO_WORD, COMMAND_UNDO_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_UNDO_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'redo' command usage instruction */
    private static String getUsageInfoForRedoCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_REDO_WORD, COMMAND_REDO_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_REDO_EXAMPLE) + LS;
    }

    /** Returns string for showing 'clear' command usage instruction */
    private static String getUsageInfoForClearCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_CLEAR_WORD, COMMAND_CLEAR_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_CLEAR_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'list' command usage instruction */
    private static String getUsageInfoForViewCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_LIST_WORD, COMMAND_LIST_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_LIST_PARAMETER) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_LIST_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'edit' command usage instruction */
    private static String getUsageInfoForEditCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_EDIT_WORD, COMMAND_EDIT_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_PARAMETERS, COMMAND_EDIT_PARAMETERS) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_EDIT_EXAMPLE) + LS;
    }

    /** Returns string for showing 'help' command usage instruction */
    private static String getUsageInfoForHelpCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_HELP_WORD, COMMAND_HELP_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_HELP_EXAMPLE) + LS;
    }

    /** Returns the string for showing 'exit' command usage instruction */
    private static String getUsageInfoForExitCommand() {
        return String.format(MESSAGE_COMMAND_HELP, COMMAND_EXIT_WORD, COMMAND_EXIT_DESC) + LS
                + String.format(MESSAGE_COMMAND_HELP_EXAMPLE, COMMAND_EXIT_EXAMPLE) + LS;
    }


    /*
     * ============================
     *         UTILITY METHODS
     * ============================
     */

    /**
     * Removes sign(p/, d/, etc) from parameter string if it is the first substring of the full string
     *
     * @param fullString  Parameter as a string
     * @param prefixSign  Parameter sign to be removed
     * @return  string without the sign or full string if invalid
     */
    private static String removeFirstPrefixSign(String fullString, String prefixSign) {
        String checkStringPrefix = fullString.substring(0, prefixSign.length());
        if (checkStringPrefix.equals(prefixSign))
            return fullString.substring(prefixSign.length());
        else {
            return fullString;
        }
    }

    /**
     * Splits a source string into the list of substrings that were separated by whitespace.
     *
     * @param toSplit source string
     * @return split by whitespace
     */
    private static ArrayList<String> splitByWhitespace(String toSplit) {
        return new ArrayList<>(Arrays.asList(toSplit.trim().split("\\s+")));
    }

}