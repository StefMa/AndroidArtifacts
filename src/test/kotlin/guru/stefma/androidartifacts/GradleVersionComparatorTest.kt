package guru.stefma.androidartifacts

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class GradleVersionComparatorTest {

    @Test
    fun `Gradle 4dot8 is better than 4dot7`() {
        val betterThan = GradleVersionComparator("4.8").betterThan("4.7")

        assertThat(betterThan).isTrue()
    }

    @Test
    fun `Gradle 4dot10dot1 is better than 4dot10`() {
        val betterThan = GradleVersionComparator("4.10.1").betterThan("4.10")

        assertThat(betterThan).isTrue()
    }

    @Test
    fun `Gradle 4dot8 is not better than 4dot9`() {
        val betterThan = GradleVersionComparator("4.8").betterThan("4.9")

        assertThat(betterThan).isFalse()
    }

    @Test
    fun `Gradle 4dot9 is not better than 4dot10dot2`() {
        val betterThan = GradleVersionComparator("4.9").betterThan("4.10.2")

        assertThat(betterThan).isFalse()
    }

    @Test
    fun `Gradle 5dot0 is better than 4dot10dot2`() {
        val betterThan = GradleVersionComparator("5.0").betterThan("4.10.2")

        assertThat(betterThan).isTrue()
    }

    @Test
    fun `Gradle 4dot10dot1 is smaller than 4dot10dot2`() {
        val betterThan = GradleVersionComparator("4.10.1").smallerThan("4.10.2")

        assertThat(betterThan).isTrue()
    }

    @Test
    fun `Gradle 4dot6 is smaller than 4dot10dot1`() {
        val betterThan = GradleVersionComparator("4.6").smallerThan("4.10.1")

        assertThat(betterThan).isTrue()
    }

    @Test
    fun `Gradle 4dot10dot1 is not smaller than 4dot10`() {
        val betterThan = GradleVersionComparator("4.10.1").smallerThan("4.10")

        assertThat(betterThan).isFalse()
    }

    @Test
    fun `Gradle 5dot0 is not smaller than 4dot10dot2`() {
        val betterThan = GradleVersionComparator("5.0").smallerThan("4.10.2")

        assertThat(betterThan).isFalse()
    }
}