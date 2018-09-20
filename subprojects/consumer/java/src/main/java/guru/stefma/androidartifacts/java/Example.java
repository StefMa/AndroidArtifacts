package guru.stefma.androidartifacts.java;

import org.mockito.Mockito;

/**
 * This <b>Exmaple</b> class will only be used to
 * show how to use the <i>guru.stefma.javaartifacts</i> Gradle plugin.
 */
public class Example {

    /**
     * This static method will print the given <b>string</b> parameter
     * to the {@link System#out}
     *
     * @param string the string which will be displayed
     */
    public static void displayString(final String string) {
        System.out.println(string);
    }

    /**
     * Just a protected method
     */
    protected void canBeOverriden() {

    }

    /**
     * An example that shows that classes
     * from dependencies (like the {@link org.junit.runners.JUnit4}) are
     * inside the javadoc.
     *
     * @return a valid {@link Mockito} instance.
     */
    public Mockito ignoreMe() {
        return new Mockito();
    }

}