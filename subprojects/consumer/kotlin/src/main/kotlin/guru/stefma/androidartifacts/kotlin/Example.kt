package guru.stefma.androidartifacts.kotlin

import java.lang.System

/**
 * This **Exmaple** class will only be used to
 * show how to use the *guru.stefma.javaartifacts* Gradle plugin.
 */
class Example {

    companion object {

        /**
         * This static method will print the given **string** parameter
         * to the [System.out]
         *
         * @param string the string which will be displayed
         */
        fun displayString(string: String) {
            System.out.println(string);
        }

    }

    /**
     * Just a protected method
     */
    protected fun canBeOverriden() {

    }

}