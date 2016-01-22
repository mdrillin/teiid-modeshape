/*
 * JBoss, Home of Professional Open Source.
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 * 02110-1301 USA.
 */
package org.teiid.modeshape.sequencer.ddl;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.modeshape.common.text.ParsingException;
import org.modeshape.common.util.StringUtil;
import org.teiid.modeshape.sequencer.ddl.TeiidDdlConstants.TeiidReservedWord;
import org.teiid.modeshape.sequencer.ddl.node.AstNode;

/**
 * A test class for {@link StatementParser}.
 */
public class StatementParserTest extends TeiidDdlTest {

    private TestParser parser;
    private AstNode rootNode;

    @Before
    public void beforeEach() {
        final TeiidDdlParser teiidDdlParser = new TeiidDdlParser();
        this.parser = new TestParser(teiidDdlParser);
        this.rootNode = teiidDdlParser.nodeFactory().node("ddlRootNode");
    }

    // ********* parse identifier tests ***********

    @Test
    public void shouldParseUnqualifiedIdentifier() {
        final String id = "name";
        assertThat(this.parser.parseIdentifier(getTokens(id)), is(id));
    }

    @Test
    public void shouldParseDoubleQuotedUnqualifiedIdentifier() {
        final String id = "name";
        final String quotedId = '"' + id + '"';
        assertThat(this.parser.parseIdentifier(getTokens(quotedId)), is(id));
    }

    @Test
    public void shouldParseSingleQuotedUnqualifiedIdentifier() {
        final String id = "name";
        final String quotedId = '\'' + id + '\'';
        assertThat(this.parser.parseIdentifier(getTokens(quotedId)), is(id));
    }

    @Test
    public void shouldParseQualifiedIdentifier() {
        final String id = "first.second";
        assertThat(this.parser.parseIdentifier(getTokens(id)), is(id));
    }

    @Test
    public void shouldParseDoubleQuotedQualifiedIdentifier() {
        final String firstSegment = "first";
        final String secondSegment = "second";
        final String id = firstSegment + '.' + secondSegment;
        final String quotedId = '"' + firstSegment + '"' + '.' + '"' + secondSegment + '"';
        assertThat(this.parser.parseIdentifier(getTokens(quotedId)), is(id));
    }

    @Test
    public void shouldParseSingleQuotedQualifiedIdentifier() {
        final String firstSegment = "first";
        final String secondSegment = "second";
        final String id = firstSegment + '.' + secondSegment;
        final String quotedId = '\'' + firstSegment + "'.'" + secondSegment + '\'';
        assertThat(this.parser.parseIdentifier(getTokens(quotedId)), is(id));
    }

    // ********* parse options clause tests ***********

    @Test
    public void shouldNotAddEmptyValuesOption() {
        assertOptionsClause(4,
                            "a",
                            "",
                            "b",
                            "+1.234",
                            "c",
                            "-0.987",
                            "d",
                            "",
                            "e",
                            "`accounts`.`ACCOUNT`",
                            "f",
                            "accounts.ACCOUNT",
                            "g",
                            "");
    }

    @Test
    public void shouldNotAddSingleOptionEmptyValue() {
        assertOptionsClause(0, "key", "");
    }

    @Test
    public void shouldNotAddMultipleOptionsAllEmptyValues() {
        assertOptionsClause(0, "a", "", "b", "", "c", "", "d", "", "e", "", "f", "", "g", "");
    }

    @Test
    public void shouldParseSingleOption() {
        assertOptionsClause("key", "value");
    }

    @Test
    public void shouldParseSingleOptionPositiveDecimal() {
        assertOptionsClause("key", "+1.234");
    }

    @Test
    public void shouldParseSingleOptionNegativeDecimal() {
        assertOptionsClause("key", "-0.987");
    }

    @Test
    public void shouldParseSingleOptionQuotedUnqualifiedValue() {
        assertOptionsClause("key", "`value`");
    }

    @Test
    public void shouldParseSingleOptionQuotedQualifiedValue() {
        assertOptionsClause("key", "`accounts`.`ACCOUNT`");
    }

    @Test
    public void shouldParseSingleOptionQualifiedValue() {
        assertOptionsClause("key", "accounts.ACCOUNT");
    }

    @Test
    public void shouldParseSingleOptionBoolean() {
        assertOptionsClause("key", "TRUE");
    }

    @Test
    public void shouldParseMultipleOptions() {
        assertOptionsClause("a",
                            "value",
                            "b",
                            "+1.234",
                            "c",
                            "-0.987",
                            "d",
                            "`value`",
                            "e",
                            "`accounts`.`ACCOUNT`",
                            "f",
                            "accounts.ACCOUNT",
                            "g",
                            "TRUE");
    }

    private void assertOptionsClause( int expectedNumberOfOptions,
                                      final String... optionPairs ) {
        final StringBuilder optionsClause = new StringBuilder(TeiidReservedWord.OPTIONS.toDdl() + " ( ");

        for (int i = 0; i < optionPairs.length; ++i) {
            if (i > 1) {
                optionsClause.append(", ");
            }

            optionsClause.append('\'').append(optionPairs[i]).append('\'').append(' ');
            optionsClause.append('\'').append(optionPairs[++i]).append('\'');
        }

        optionsClause.append(" )");
        assertThat(this.parser.parseOptionsClause(getTokens(optionsClause.toString()), this.rootNode), is(true));

        final List<AstNode> optionNodes = this.rootNode.getChildren(StandardDdlLexicon.TYPE_STATEMENT_OPTION);
        assertThat(optionNodes.size(), is(expectedNumberOfOptions));

        for (int i = 0; i < optionPairs.length; ++i) {
            final String key = optionPairs[i];
            final String value = optionPairs[++i];

            if (!StringUtil.isBlank(value)) {
                boolean found = false;

                for (final AstNode optionNode : optionNodes) {
                    if (key.equals(optionNode.getName()) && value.equals(optionNode.getProperty(StandardDdlLexicon.VALUE))) {
                        found = true;
                        break;
                    }
                }

                if (!found) {
                    fail("option '" + key + "' with value '" + value + "' was not found");
                }
            }
        }
    }

    private void assertOptionsClause( final String... optionPairs ) {
        assertOptionsClause((optionPairs.length / 2), optionPairs);
    }

    class TestParser extends StatementParser {

        protected TestParser( TeiidDdlParser teiidDdlParser ) {
            super(teiidDdlParser);
        }

        @Override
        boolean matches( DdlTokenStream tokens ) {
            return false;
        }

        @Override
        AstNode parse( DdlTokenStream tokens,
                       AstNode parentNode ) throws ParsingException {
            return null;
        }

        @Override
        protected void postProcess( AstNode rootNode ) {

        }

    }
}
