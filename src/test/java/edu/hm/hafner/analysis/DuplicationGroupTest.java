package edu.hm.hafner.analysis;

import java.io.IOException;

import org.junit.jupiter.api.Test;

import edu.hm.hafner.util.LineRange;
import edu.hm.hafner.util.LineRangeList;
import edu.hm.hafner.util.SerializableTest;
import edu.hm.hafner.util.TreeString;

import nl.jqno.equalsverifier.EqualsVerifier;
import nl.jqno.equalsverifier.Warning;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests the class {@link DuplicationGroup}.
 *
 * @author Ullrich Hafner
 */
class DuplicationGroupTest extends SerializableTest<DuplicationGroup> {
    private static final String SERIALIZATION_NAME = "dry.ser";
    private static final String CODE_FRAGMENT = "fragment";

    @Override
    protected DuplicationGroup createSerializable() {
        var group = new DuplicationGroup();

        try (var builder = new IssueBuilder()) {
            group.add(builder.setAdditionalProperties(group).setFileName("1").build());
            group.add(builder.setAdditionalProperties(group).setFileName("2").build());
        }

        return group;
    }

    @Override
    protected void assertThatRestoredInstanceEqualsOriginalInstance(
            final DuplicationGroup original, final DuplicationGroup restored) {
        assertThat(original).isEqualTo(restored);
    }

    /**
     * Verifies that saved serialized format (from a previous release) still can be resolved with the current
     * implementation of {@link DuplicationGroup}.
     */
    @Test
    void shouldReadIssueFromOldSerialization() {
        var restored = readAllBytes(SERIALIZATION_NAME);

        assertThatSerializableCanBeRestoredFrom(restored);
    }

    @Test
    void shouldBeEmptyWhenCreated() {
        var group = new DuplicationGroup();

        assertThat(group.getCodeFragment()).isEmpty();
        assertThat(group.getDuplications()).isEmpty();
    }

    @Test
    void shouldNotOverwriteFragmentOnceItHasBeenSet() {
        var group = new DuplicationGroup();

        assertThat(group.getCodeFragment()).isEmpty();

        group.setCodeFragment(CODE_FRAGMENT);
        assertThat(group.getCodeFragment()).isEqualTo(CODE_FRAGMENT);

        group.setCodeFragment("other");
        assertThat(group.getCodeFragment()).isEqualTo(CODE_FRAGMENT);

        var groupWithFragment = new DuplicationGroup(CODE_FRAGMENT);
        assertThat(groupWithFragment.getCodeFragment()).isEqualTo(CODE_FRAGMENT);

        groupWithFragment.setCodeFragment("other");
        assertThat(groupWithFragment.getCodeFragment()).isEqualTo(CODE_FRAGMENT);
    }

    @Test
    void shouldReferenceAllDuplications() {
        try (var builder = new IssueBuilder()) {
            var group = new DuplicationGroup(CODE_FRAGMENT);

            assertThat(group.getDuplications()).isEmpty();

            var first = builder.setAdditionalProperties(group).build();
            var second = builder.setAdditionalProperties(group).build();

            group.add(first);
            group.add(second);

            assertThat(group.getDuplications()).containsExactly(first, second);
            assertThat(first.getAdditionalProperties()).isEqualTo(second.getAdditionalProperties());
        }
    }

    @Test
    void shouldObeyEqualsContract() {
        try (var builder = new IssueBuilder()) {
            var red = new LineRangeList();
            red.add(new LineRange(1));
            var blue = new LineRangeList();
            blue.add(new LineRange(2));
            EqualsVerifier
                    .forClass(DuplicationGroup.class)
                    .withOnlyTheseFields("codeFragment")
                    .withPrefabValues(LineRangeList.class, red, blue)
                    .withPrefabValues(Issue.class, builder.setFileName("red").build(), builder.setFileName("blue").build())
                    .withPrefabValues(TreeString.class, TreeString.valueOf("red"), TreeString.valueOf("blue"))
                    .suppress(Warning.NONFINAL_FIELDS)
                    .verify();
        }
    }

    /**
     * Serializes a code duplication to a file. Use this method in case the properties have been changed and the
     * readResolve method has been adapted accordingly so that the old serialization still can be read.
     *
     * @param args
     *         not used
     *
     * @throws IOException
     *         if the file could not be written
     */
    public static void main(final String... args) throws IOException {
        new DuplicationGroupTest().createSerializationFile();
    }
}
