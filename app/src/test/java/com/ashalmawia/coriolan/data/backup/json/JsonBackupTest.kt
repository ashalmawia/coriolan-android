package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.learning.ExerciseDescriptor
import com.ashalmawia.coriolan.learning.exercise.MockExerciseDescriptor
import com.ashalmawia.coriolan.learning.scheduler.StateType
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(JUnit4::class)
class JsonBackupTest {

    private val backup: Backup = JsonBackup()

    @Test
    fun `test__emptyRepository`() {
        // given
        val repo = MockBackupableRepository.empty()
        val exercises = listOf(
                MockExerciseDescriptor(stateType = StateType.SR_STATE),
                MockExerciseDescriptor(stateType = StateType.UNKNOWN)
        )

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__nonEmptyRepository_noStates`() {
        // given
        val repo = MockBackupableRepository.random(0)

        // then
        test(repo, repo.exercises(), backup)
    }

    @Test
    fun `test__singleSRStateExercise`() {
        // given
        val repo = MockBackupableRepository.random(1)

        // then
        test(repo, repo.exercises(), backup)
    }

    @Test
    fun `test__multipleSRStateExercises`() {
        // given
        val repo = MockBackupableRepository.random(5)

        // then
        test(repo, repo.exercises(), backup)
    }

    @Test
    fun `test__multipleSRStateExercisesAndSomeWithoutState`() {
        // given
        val repo = MockBackupableRepository.random(5)

        // then
        test(repo, repo.exercises().plus(MockExerciseDescriptor("no_state", StateType.UNKNOWN)), backup)
    }

    @Test
    fun `test__applyEmptyToANonEmpty`() {
        // given
        val repo = MockBackupableRepository.empty()
        val outRepo = MockBackupableRepository.random(3)

        // then
        test(repo, repo.exercises(), backup, outRepo)
    }

    @Test
    fun `test__applyNonEmptyToANonEmpty`() {
        // given
        val repo = MockBackupableRepository.random(5)
        val outRepo = MockBackupableRepository.random(3)

        // then
        test(repo, repo.exercises(), backup, outRepo)
    }

    @Test
    fun `test__smallPage`() {
        // given
        val backup = JsonBackup(2)

        val repo = MockBackupableRepository.random(2)

        // then
        test(repo, repo.exercises(), backup)
    }

    @Test
    fun `test__mediumPage`() {
        // given
        val backup = JsonBackup(5)

        val repo = MockBackupableRepository.random(2)

        // then
        test(repo, repo.exercises(), backup)
    }

    @Test
    fun `test__bigPage`() {
        // given
        val backup = JsonBackup(50)

        val repo = MockBackupableRepository.random(2)

        // then
        test(repo, repo.exercises(), backup)
    }
}

private fun test(
        repo: MockBackupableRepository,
        exercises: List<ExerciseDescriptor<*, *>>,
        backup: Backup,
        outRepo: MockBackupableRepository = MockBackupableRepository.empty()
) {
    // when
    val output = ByteArrayOutputStream()

    // when
    backup.create(repo, exercises, output)

    println(output.toString())

    // given
    val input = ByteArrayInputStream(output.toByteArray())

    // when
    backup.restoreFrom(input, outRepo)

    // then
    MockBackupableRepository.assertEquals(repo, outRepo)
}