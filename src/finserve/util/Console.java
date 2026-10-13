package finserve.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Small helper around Scanner that gives the whole CLI a consistent look.
 */
public final class Console {

    private static final Scanner SC = new Scanner(System.in);

    private Console() { }

    public static Scanner scanner() {
        return SC;
    }

    public static void line() {
        System.out.println("--------------------------------------------------------------------------");
    }

    public static void title(String text) {
        line();
        System.out.println("  " + text);
        line();
    }

    public static void section(String text) {
        System.out.println();
        System.out.println(">> " + text);
    }

    public static void info(String text) {
        System.out.println("   " + text);
    }

    public static void result(String text) {
        System.out.println("   [RESULT] " + text);
    }

    public static void ok(String text) {
        System.out.println("   [OK] " + text);
    }

    public static void warn(String text) {
        System.out.println("   [!] " + text);
    }

    public static void pause() {
        System.out.print("\n   Press ENTER to continue...");
        SC.nextLine();
    }

    public static String readLine(String prompt) {
        System.out.print("   " + prompt);
        return SC.nextLine().trim();
    }

    public static String readNonEmpty(String prompt) {
        while (true) {
            String s = readLine(prompt);
            if (!s.isEmpty()) {
                return s;
            }
            warn("Input cannot be empty.");
        }
    }

    public static int readInt(String prompt, int min, int max) {
        while (true) {
            String s = readLine(prompt);
            try {
                int v = Integer.parseInt(s);
                if (v >= min && v <= max) {
                    return v;
                }
                warn("Enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                warn("Not a valid integer.");
            }
        }
    }

    public static int readInt(String prompt) {
        return readInt(prompt, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public static int readInt(String prompt, int min) {
        return readInt(prompt, min, Integer.MAX_VALUE);
    }

    public static long readLong(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Long.parseLong(s);
            } catch (NumberFormatException e) {
                warn("Not a valid long integer.");
            }
        }
    }

    public static double readDouble(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                return Double.parseDouble(s);
            } catch (NumberFormatException e) {
                warn("Not a valid number.");
            }
        }
    }

    /** Reads a line of comma separated integers, e.g. "5, 3, 8, 1". */
    public static int[] readIntArray(String prompt) {
        while (true) {
            String s = readLine(prompt);
            try {
                String[] parts = s.split(",");
                int[] arr = new int[parts.length];
                for (int i = 0; i < parts.length; i++) {
                    arr[i] = Integer.parseInt(parts[i].trim());
                }
                if (arr.length == 0) {
                    throw new NumberFormatException("empty");
                }
                return arr;
            } catch (NumberFormatException e) {
                warn("Use comma separated integers, e.g. 5,3,8,1");
            }
        }
    }

    /** Reads N whitespace/comma separated tokens. */
    public static List<String> readTokens(String prompt, int count) {
        while (true) {
            String s = readLine(prompt);
            String[] parts = s.split("[,\\s]+");
            List<String> out = new ArrayList<String>();
            for (String p : parts) {
                if (!p.isEmpty()) {
                    out.add(p);
                }
            }
            if (out.size() == count) {
                return out;
            }
            warn("Expected " + count + " items but got " + out.size() + ".");
        }
    }

    public static boolean confirm(String prompt) {
        while (true) {
            String s = readLine(prompt + " (y/n)").toLowerCase();
            if (s.equals("y") || s.equals("yes")) return true;
            if (s.equals("n") || s.equals("no")) return false;
            warn("Answer y or n.");
        }
    }
}
