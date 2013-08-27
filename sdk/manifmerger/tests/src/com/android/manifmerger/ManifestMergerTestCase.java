/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Eclipse Public License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.eclipse.org/org/documents/epl-v10.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.manifmerger;

import com.android.annotations.NonNull;
import com.android.sdklib.mock.MockLog;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

/**
 * Some utilities to reduce repetitions in the {@link ManifestMergerTest}s.
 * <p/>
 * See {@link #loadTestData(String)} for an explanation of the data file format.
 */
abstract class ManifestMergerTestCase extends TestCase {

    /**
     * Delimiter that indicates the test must fail.
     * An XML output and errors are still generated and checked.
     */
    private static final String DELIM_FAILS  = "fails";
    /**
     * Delimiter that starts a library XML content.
     * The delimiter name must be in the form {@code @libSomeName} and it will be
     * used as the base for the test file name. Using separate lib names is encouraged
     * since it makes the error output easier to read.
     */
    private static final String DELIM_LIB    = "lib";
    /**
     * Delimiter that starts the main manifest XML content.
     */
    private static final String DELIM_MAIN   = "main";
    /**
     * Delimiter that starts the resulting XML content, whatever is generated by the merge.
     */
    private static final String DELIM_RESULT = "result";
    /**
     * Delimiter that starts the SdkLog output.
     * The logger prints each entry on its lines, prefixed with E for errors,
     * W for warnings and P for regular printfs.
     */
    private static final String DELIM_ERRORS = "errors";

    static class TestFiles {
        private final File mMain;
        private final File[] mLibs;
        private final File mActualResult;
        private final String mExpectedResult;
        private final String mExpectedErrors;
        private final boolean mShouldFail;

        /** Files used by a given test case. */
        public TestFiles(
                boolean shouldFail,
                @NonNull File main,
                @NonNull File[] libs,
                @NonNull File actualResult,
                @NonNull String expectedResult,
                @NonNull String expectedErrors) {
            mShouldFail = shouldFail;
            mMain = main;
            mLibs = libs;
            mActualResult = actualResult;
            mExpectedResult = expectedResult;
            mExpectedErrors = expectedErrors;
        }

        public boolean getShouldFail() {
            return mShouldFail;
        }

        @NonNull
        public File getMain() {
            return mMain;
        }

        @NonNull
        public File[] getLibs() {
            return mLibs;
        }

        @NonNull
        public File getActualResult() {
            return mActualResult;
        }

        @NonNull
        public String getExpectedResult() {
            return mExpectedResult;
        }

        public String getExpectedErrors() {
            return mExpectedErrors;
        }

        // Try to delete any temp file potentially created.
        public void cleanup() {
            if (mMain != null && mMain.isFile()) {
                mMain.delete();
            }

            if (mActualResult != null && mActualResult.isFile()) {
                mActualResult.delete();
            }

            for (File f : mLibs) {
                if (f != null && f.isFile()) {
                    f.delete();
                }
            }
        }
    }

    /**
     * Calls {@link #loadTestData(String)} by
     * inferring the data filename from the caller's method name.
     * <p/>
     * The caller method name must be composed of "test" + the leaf filename.
     * Extensions ".xml" or ".txt" are implied.
     * <p/>
     * E.g. to use the data file "12_foo.xml", simply call this from a method
     * named "test12_foo".
     *
     * @return A new {@link TestFiles} instance. Never null.
     * @throws Exception when things go wrong.
     * @see #loadTestData(String)
     */
    @NonNull
    TestFiles loadTestData() throws Exception {
        StackTraceElement[] stack = Thread.currentThread().getStackTrace();
        for (int i = 0, n = stack.length; i < n; i++) {
            StackTraceElement caller = stack[i];
            String name = caller.getMethodName();
            if (name.startsWith("test")) {
                return loadTestData(name.substring(4));
            }
        }

        throw new IllegalArgumentException("No caller method found which name started with 'test'");
    }

    /**
     * Loads test data for a given test case.
     * The input (main + libs) are stored in temp files.
     * A new destination temp file is created to store the actual result output.
     * The expected result is actually kept in a string.
     * <p/>
     * Data File Syntax:
     * <ul>
     * <li> Lines starting with # are ignored (anywhere, as long as # is the first char).
     * <li> Lines before the first {@code @delimiter} are ignored.
     * <li> Empty lines just after the {@code @delimiter}
     *      and before the first &lt; XML line are ignored.
     * <li> Valid delimiters are {@code @main} for the XML of the main app manifest.
     * <li> Following delimiters are {@code @libXYZ}, read in the order of definition.
     *      The name can be anything as long as it starts with "{@code @lib}".
     * </ul>
     *
     * @param filename The test data filename. If no extension is provided, this will
     *   try with .xml or .txt. Must not be null.
     * @return A new {@link TestFiles} instance. Must not be null.
     * @throws Exception when things fail to load properly.
     */
    @NonNull
    TestFiles loadTestData(@NonNull String filename) throws Exception {

        String resName = "data" + File.separator + filename;
        InputStream is = null;
        BufferedReader reader = null;
        BufferedWriter writer = null;

        try {
            is = this.getClass().getResourceAsStream(resName);
            if (is == null && !filename.endsWith(".xml")) {
                String resName2 = resName + ".xml";
                is = this.getClass().getResourceAsStream(resName2);
                if (is != null) {
                    filename = resName2;
                }
            }
            if (is == null && !filename.endsWith(".txt")) {
                String resName3 = resName + ".txt";
                is = this.getClass().getResourceAsStream(resName3);
                if (is != null) {
                    filename = resName3;
                }
            }
            assertNotNull("Test data file not found for " + filename, is);

            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            // Get the temporary directory to use. Just create a temp file, extracts its
            // directory and remove the file.
            File tempFile = File.createTempFile(this.getClass().getSimpleName(), ".tmp");
            File tempDir = tempFile.getParentFile();
            if (!tempFile.delete()) {
                tempFile.deleteOnExit();
            }

            String line = null;
            String delimiter = null;
            boolean skipEmpty = true;

            boolean shouldFail = false;
            StringBuilder expectedResult = new StringBuilder();
            StringBuilder expectedErrors = new StringBuilder();
            File mainFile = null;
            File actualResultFile = null;
            List<File> libFiles = new ArrayList<File>();
            int tempIndex = 0;

            while ((line = reader.readLine()) != null) {
                if (skipEmpty && line.trim().length() == 0) {
                    continue;
                }
                if (line.length() > 0 && line.charAt(0) == '#') {
                    continue;
                }
                if (line.length() > 0 && line.charAt(0) == '@') {
                    delimiter = line.substring(1);
                    assertTrue(
                        "Unknown delimiter @" + delimiter + " in " + filename,
                        delimiter.startsWith(DELIM_LIB) ||
                        delimiter.equals(DELIM_MAIN) ||
                        delimiter.equals(DELIM_RESULT) ||
                        delimiter.equals(DELIM_ERRORS) ||
                        delimiter.equals(DELIM_FAILS));

                    skipEmpty = true;

                    if (writer != null) {
                        try {
                            writer.close();
                        } catch (IOException ignore) {}
                        writer = null;
                    }

                    if (delimiter.equals(DELIM_FAILS)) {
                        shouldFail = true;

                    } else if (!delimiter.equals(DELIM_ERRORS)) {
                        tempFile = new File(tempDir, String.format("%1$s%2$d_%3$s.xml",
                                this.getClass().getSimpleName(),
                                tempIndex++,
                                delimiter.replaceAll("[^a-zA-Z0-9_-]", "")
                                ));
                        tempFile.deleteOnExit();

                        if (delimiter.startsWith(DELIM_LIB)) {
                            libFiles.add(tempFile);

                        } else if (delimiter.equals(DELIM_MAIN)) {
                            mainFile = tempFile;

                        } else if (delimiter.equals(DELIM_RESULT)) {
                            actualResultFile = tempFile;

                        } else {
                            fail("Unexpected data file delimiter @" + delimiter +
                                 " in " + filename);
                        }

                        if (!delimiter.equals(DELIM_RESULT)) {
                            writer = new BufferedWriter(new FileWriter(tempFile));
                        }
                    }

                    continue;
                }
                if (delimiter != null &&
                        skipEmpty &&
                        line.length() > 0 &&
                        line.charAt(0) != '#' &&
                        line.charAt(0) != '@') {
                    skipEmpty = false;
                }
                if (writer != null) {
                    writer.write(line);
                    writer.write('\n');
                } else if (DELIM_RESULT.equals(delimiter)) {
                    expectedResult.append(line).append('\n');
                } else if (DELIM_ERRORS.equals(delimiter)) {
                    expectedErrors.append(line).append('\n');
                }
            }

            assertNotNull("Missing @" + DELIM_MAIN + " in " + filename, mainFile);
            assertNotNull("Missing @" + DELIM_RESULT + " in " + filename, actualResultFile);

            return new TestFiles(
                    shouldFail,
                    mainFile,
                    libFiles.toArray(new File[libFiles.size()]),
                    actualResultFile,
                    expectedResult.toString(),
                    expectedErrors.toString());

        } catch (UnsupportedEncodingException e) {
            // BufferedReader failed to decode UTF-8, O'RLY?
            throw e;

        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException ignore) {}
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignore) {}
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException ignore) {}
            }
        }
    }

    /**
     * Loads the data test files using {@link #loadTestData()} and then
     * invokes {@link #processTestFiles(TestFiles)} to test them.
     *
     * @see #loadTestData()
     * @see #processTestFiles(TestFiles)
     */
    void processTestFiles() throws Exception {
        processTestFiles(loadTestData());
    }

    /**
     * Processes the data from the given {@link TestFiles} by
     * invoking {@link ManifestMerger#process(File, File, File[])}:
     * the given library files are applied consecutively to the main XML
     * document and the output is generated.
     * <p/>
     * Then the expected and actual outputs are loaded into a DOM,
     * dumped again to a String using an XML transform and compared.
     * This makes sure only the structure is checked and that any
     * formatting is ignored in the comparison.
     *
     * @param testFiles The test files to process. Must not be null.
     * @throws Exception when this go wrong.
     */
    void processTestFiles(TestFiles testFiles) throws Exception {
        MockLog log = new MockLog();
        ManifestMerger merger = new ManifestMerger(log);
        boolean processOK = merger.process(testFiles.getActualResult(),
                                  testFiles.getMain(),
                                  testFiles.getLibs());

        String expectedErrors = testFiles.getExpectedErrors().trim();
        StringBuilder actualErrors = new StringBuilder();
        for (String s : log.getMessages()) {
            actualErrors.append(s);
            if (!s.endsWith("\n")) {
                actualErrors.append('\n');
            }
        }
        assertEquals("Error generated during merging",
                expectedErrors, actualErrors.toString().trim());

        if (testFiles.getShouldFail()) {
            assertFalse("Merge process() returned true, expected false", processOK);
        } else {
            assertTrue("Merge process() returned false, expected true", processOK);
        }

        // Test result XML. There should always be one created
        // since the process action does not stop on errors.
        log.clear();
        String actual = XmlUtils.printXmlString(
                            XmlUtils.parseDocument(testFiles.getActualResult(), log),
                            log);
        assertEquals("Error parsing actual result XML", "[]", log.toString());
        log.clear();
        String expected = XmlUtils.printXmlString(
                            XmlUtils.parseDocument(testFiles.getExpectedResult(), log),
                            log);
        assertEquals("Error parsing expected result XML", "[]", log.toString());
        assertEquals("Error comparing expected to actual result", expected, actual);

        testFiles.cleanup();
    }

}
