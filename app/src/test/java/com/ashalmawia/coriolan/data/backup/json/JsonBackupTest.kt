package com.ashalmawia.coriolan.data.backup.json

import com.ashalmawia.coriolan.data.backup.Backup
import com.ashalmawia.coriolan.data.backup.BackupableRepository
import com.ashalmawia.coriolan.data.backup.SRStateInfo
import com.ashalmawia.coriolan.learning.Exercise
import com.ashalmawia.coriolan.learning.exercise.MockExercise
import com.ashalmawia.coriolan.learning.StateType
import junit.framework.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

@RunWith(JUnit4::class)
abstract class JsonBackupTest {

    private val backup: Backup = JsonBackup()
    
    protected abstract fun createEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository
    protected abstract fun createNonEmptyRepo(exercises: List<Exercise<*, *>>): BackupableRepository

    private fun exercises(count: Int = 0): List<Exercise<*, *>> {
        return (1..count).map { MockExercise("exercise_$it", StateType.SR_STATE) }
    }

    @Test
    fun `test__emptyRepository`() {
        // given
        val exercises = listOf(
                MockExercise(stateType = StateType.SR_STATE),
                MockExercise(stateType = StateType.UNKNOWN)
        )
        val repo = createEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__nonEmptyRepository_noStates`() {
        // given
        val exercises = exercises()
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__singleSRStateExercise`() {
        // given
        val exercises = exercises(1)
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__multipleSRStateExercises`() {
        // given
        val exercises = exercises(5)
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__multipleSRStateExercisesAndSomeWithoutState`() {
        // given
        val exercises = exercises(5).plus(MockExercise("no_state", StateType.UNKNOWN))
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__applyEmptyToANonEmpty`() {
        // given
        val exercises = exercises(3)

        val repo = createEmptyRepo(exercises)
        val outRepo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup, outRepo)
    }

    @Test
    fun `test__applyNonEmptyToANonEmpty`() {
        // given
        val exercisesIn = exercises(3)
        val exercises = exercises(5)

        val repo = createNonEmptyRepo(exercisesIn)
        val outRepo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup, outRepo, exercisesIn)
    }

    @Test
    fun `test__applyNonEmptyToANonEmpty__lessExercises`() {
        // given
        val exercisesIn = exercises(5)
        val exercises = exercises(3)

        val repo = createNonEmptyRepo(exercisesIn)
        val outRepo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup, outRepo, exercisesIn)
    }

    @Test
    fun `test__smallPage`() {
        // given
        val backup = JsonBackup(2)

        val exercises = exercises(2)
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__mediumPage`() {
        // given
        val backup = JsonBackup(5)

        val exercises = exercises(2)
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    @Test
    fun `test__bigPage`() {
        // given
        val backup = JsonBackup(50)

        val exercises = exercises(2)
        val repo = createNonEmptyRepo(exercises)

        // then
        test(repo, exercises, backup)
    }

    private fun test(
            repo: BackupableRepository,
            exercises: List<Exercise<*, *>>,
            backup: Backup,
            outRepo: BackupableRepository = createEmptyRepo(exercises),
            exercisesIn: List<Exercise<*, *>> = exercises
    ) {
        // when
        val output = ByteArrayOutputStream()

        // when
        backup.create(repo, exercisesIn, output)

        println(output.toString())

        // given
        val input = ByteArrayInputStream(output.toByteArray())

        // when
        backup.restoreFrom(input, outRepo)

        // then
        assertRepoEquals(repo, outRepo, exercises, exercisesIn)
    }

    private fun assertRepoEquals(expected: BackupableRepository, actual: BackupableRepository,
                                 exercises: List<Exercise<*, *>>, exercisesIn: List<Exercise<*, *>> = exercises) {
        assertEquals(expected.allLanguages(0, 500), actual.allLanguages(0, 500))
        assertEquals(expected.allDomains(0, 500), actual.allDomains(0, 500))
        assertEquals(expected.allExpressions(0, 500), actual.allExpressions(0, 500))
        assertEquals(expected.allCards(0, 500), actual.allCards(0, 500))
        assertEquals(expected.allDecks(0, 500), actual.allDecks(0, 500))

        exercises.intersect(exercisesIn).filter { it.stateType == StateType.SR_STATE }.forEach {
            assertEquals(expected.allSRStates(it.stableId, 0, 500), actual.allSRStates(it.stableId, 0, 500))
        }
        exercises.minus(exercisesIn).filter { it.stateType == StateType.SR_STATE }.forEach {
            assertEquals(emptyList<SRStateInfo>(), actual.allSRStates(it.stableId, 0, 500))
        }
    }
}