package org.fakekoji.jobmanager.project;

import org.fakekoji.DataGenerator;
import org.fakekoji.core.AccessibleSettings;
import org.fakekoji.functional.Result;
import org.fakekoji.jobmanager.model.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class ReverseJDKProjectParserTest {

    @Rule
    public final TemporaryFolder temporaryFolder = new TemporaryFolder();

    private AccessibleSettings settings;


    @Before
    public void setup() throws IOException {
        settings = DataGenerator.getSettings(DataGenerator.initFolders(temporaryFolder));
    }

    @Test
    public void parseJDKTestProjectJobs() {
        final ReverseJDKProjectParser parser = settings.getReverseJDKProjectParser();
        final Result<Project, String> result = parser.parseJobs(DataGenerator.getJDKTestProjectJobs());
        Assert.assertEquals(
                DataGenerator.getJDKTestProject(),
                result.getValue()
        );
    }

    @Test
    public void parseJDKProjectJobs() {
        final ReverseJDKProjectParser parser = settings.getReverseJDKProjectParser();
        final Result<Project, String> result = parser.parseJobs(DataGenerator.getJDKProjectJobs());
        Assert.assertEquals(
                DataGenerator.getJDKProject(),
                result.getValue()
        );
    }

    @Test
    public void findOrCreateString() {
        final String a = "aaa";
        final String b = "bbb";
        final String c = "ccc";
        final Set<String> strings = new HashSet<>(Arrays.asList(a, b));

        final Optional<String> opt = ReverseJDKProjectParser.findConfig(strings, s -> s.equals(a));
        Assert.assertTrue(opt.isPresent());
        Assert.assertEquals(opt.get(), a);

        final Optional<String> optEmpty = ReverseJDKProjectParser.findConfig(strings, s -> s.equals(c));
        Assert.assertFalse(optEmpty.isPresent());

        final String string = ReverseJDKProjectParser.findOrCreateConfig(strings, s -> s.equals(c), () -> c);
        Assert.assertEquals(string, c);
    }
}
