/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2013 - 2023, Tapio Rautonen
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.eluder.coveralls.maven.plugin.validation;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.assertj.core.api.Assertions;
import org.eluder.coveralls.maven.plugin.domain.Git;
import org.eluder.coveralls.maven.plugin.domain.Git.Head;
import org.eluder.coveralls.maven.plugin.domain.Job;
import org.eluder.coveralls.maven.plugin.validation.ValidationError.Level;
import org.junit.jupiter.api.Test;

class JobValidatorTest {

    @Test
    void testMissingJob() {
        assertThrows(IllegalArgumentException.class, () -> {
            new JobValidator(null);
        });
    }

    @Test
    void testValidateWithoutRepoTokenOrTravis() {
        ValidationErrors errors = new JobValidator(new Job()).validate();
        Assertions.assertThat(errors).hasSize(1);
        Assertions.assertThat(errors.get(0).getLevel()).isEqualByComparingTo(Level.ERROR);
    }

    @Test
    void testValidateWithoutRepoTokenOrTravisForDryRun() {
        ValidationErrors errors = new JobValidator(new Job().withDryRun(true)).validate();
        Assertions.assertThat(errors).hasSize(1);
        Assertions.assertThat(errors.get(0).getLevel()).isEqualByComparingTo(Level.WARN);
    }

    @Test
    void testValidateWithInvalidTravis() {
        ValidationErrors errors = new JobValidator(new Job().withServiceName("travis-ci")).validate();
        Assertions.assertThat(errors).hasSize(1);
        Assertions.assertThat(errors.get(0).getLevel()).isEqualByComparingTo(Level.ERROR);
    }

    @Test
    void testValidateWithRepoToken() {
        ValidationErrors errors = new JobValidator(new Job().withRepoToken("ad3fg5")).validate();
        Assertions.assertThat(errors).isEmpty();
    }

    @Test
    void testValidateWithTravis() {
        ValidationErrors errors = new JobValidator(new Job().withServiceName("travis-ci").withServiceJobId("123")).validate();
        Assertions.assertThat(errors).isEmpty();
    }

    @Test
    void testValidateWithoutGitCommitId() {
        Git git = new Git(null, new Head(null, null, null, null, null, null), null, null);
        ValidationErrors errors = new JobValidator(new Job().withRepoToken("ad3fg5").withGit(git)).validate();
        Assertions.assertThat(errors).hasSize(1);
        Assertions.assertThat(errors.get(0).getLevel()).isEqualByComparingTo(Level.ERROR);
    }

    @Test
    void testValidateWithGit() {
        Git git = new Git(null, new Head("bc23af5", null, null, null, null, null), null, null);
        ValidationErrors errors = new JobValidator(new Job().withRepoToken("ad3fg5").withGit(git)).validate();
        Assertions.assertThat(errors).isEmpty();
    }

    @Test
    void testValidateWithParallel() {
        ValidationErrors errors = new JobValidator(new Job().withRepoToken("ad3fg5").withParallel(true)).validate();
        Assertions.assertThat(errors).isEmpty();
    }

}
