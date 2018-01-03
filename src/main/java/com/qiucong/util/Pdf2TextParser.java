/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.qiucong.util;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * This is an Ant task that will allow pdf documents to be converted using an
 * Ant task.
 *
 * @author Ben Litchfield
 */

public class Pdf2TextParser {

    public static String parse(InputStream pdfFile) throws IOException {

        try (
                PDDocument document = PDDocument.load(pdfFile)
        ) {
            PDFTextStripper stripper = new PDFTextStripper();
            stripper.setSortByPosition(false);
            stripper.setShouldSeparateByBeads(false);
            stripper.setStartPage(1);
            stripper.setEndPage(1);
            stripper.setLineSeparator(EMPTY);
            return stripper.getText(document);
        } catch (final IOException e) {
            throw e;
        }

    }
}
