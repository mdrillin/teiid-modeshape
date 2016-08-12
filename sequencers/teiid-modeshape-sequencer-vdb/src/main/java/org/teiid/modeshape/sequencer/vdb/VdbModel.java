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
package org.teiid.modeshape.sequencer.vdb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.modeshape.common.util.CheckArg;
import org.modeshape.common.util.StringUtil;
import org.teiid.modeshape.sequencer.vdb.lexicon.CoreLexicon;
import org.teiid.modeshape.sequencer.vdb.lexicon.VdbLexicon;

/**
 * A simple POJO that is used to represent the information for a model read in from a VDB manifest ("vdb.xml").
 */
public class VdbModel implements Comparable<VdbModel> {
    
    /**
     * The model definition metadata type whose model definition is DDL. Value is {@value}.
     */
    public static final String DDL_METADATA_TYPE = "DDL";

    /**
     * The model definition metadata type whose model definition is a VDB entry path to a DDL file. Value is {@value}.
     */
    public static final String DDL_FILE_METADATA_TYPE = "DDL-FILE";
    
    /**
     * The default model definition metadata type. Value is {@value}.
     */
    public static final String DEFAULT_METADATA_TYPE = DDL_METADATA_TYPE;

    private String ddlFileEntryPath;
    private String description;
    private String name;
    private String type;
    private String pathInVdb;
    private List<Source> sources = new ArrayList<Source>();
    private boolean visible = true;
    private boolean builtIn = false;
    private long checksum;
    private final Set<String> imports = new HashSet<String>();
    private List<ValidationMarker> problems = new ArrayList<ValidationMarker>();
    private final Map<String, String> properties = new HashMap<String, String>();
    private String metadata; // model definition written in DDL
    private String metadataType;

    /**
     * @param name the model name (cannot be <code>null</code> or empty)
     * @param type the model type (can be <code>null</code> or empty)
     * @param pathInVdb the model path (can be <code>null</code> or empty)
     */
    public VdbModel( String name,
                     String type,
                     String pathInVdb ) {
        CheckArg.isNotEmpty(name, "name");

        this.name = name;
        this.pathInVdb = pathInVdb;
        this.type = (StringUtil.isBlank(type) ? CoreLexicon.ModelType.PHYSICAL : type);
    }

    /**
     * @return the DDL file entry path used when metadata type is {@link #DDL_FILE_METADATA_TYPE} (never <code>null</code> but
     *         can be empty)
     */
    public String getDdlFileEntryPath() {
        return ( ( this.ddlFileEntryPath == null ) ? "" : this.ddlFileEntryPath );
    }

    /**
     * @param newValue the new DDL file entry path value (can be <code>null</code> or empty)
     */
    public void setDdlFileEntryPath( final String newValue ) {
        this.ddlFileEntryPath = newValue;
    }

    /**
     * @return the description (never <code>null</code> but can be empty)
     */
    public String getDescription() {
        return ((this.description == null) ? "" : this.description);
    }

    /**
     * @param newValue the new description value (can be <code>null</code> or empty)
     */
    public void setDescription( final String newValue ) {
        this.description = newValue;
    }

    /**
     * @return the name (never <code>null</code> or empty)
     */
    public String getName() {
        return name;
    }

    /**
     * @return the model DDL definition (can be <code>null</code> or empty)
     */
    public String getModelDefinition() {
        return this.metadata;
    }

    /**
     * @param modelDefinition the new model definition (can be <code>null</code> or empty)
     */
    public void setModelDefinition( final String modelDefinition ) {
        this.metadata = modelDefinition;
    }

    /**
     * @return the model metadata type associated with the model definition (can be <code>null</code> or empty)
     */
    public String getMetadataType() {
        return this.metadataType;
    }

    /**
     * @param metadataType the new metadata type (can be <code>null</code> or empty if default type should be used)
     */
    public void setMetadataType( final String metadataType ) {
        this.metadataType = metadataType;
    }

    /**
     * @return the overridden properties (never <code>null</code>)
     */
    public Map<String, String> getProperties() {
        return this.properties;
    }

    /**
     * @return the type (never <code>null</code> or empty)
     */
    public String getType() {
        return type;
    }

    /**
     * @return visible
     */
    public boolean isVisible() {
        return visible;
    }

    /**
     * @param visible Sets visible to the specified value.
     */
    public void setVisible( boolean visible ) {
        this.visible = visible;
    }

    /**
     * @return builtIn
     */
    public boolean isBuiltIn() {
        return builtIn;
    }

    /**
     * @return <code>true</code> if model definition is declared in the VDB manifest and not by an XMI file
     */
    public boolean isDeclarative() {
        return !getProperties().containsKey(VdbLexicon.ManifestIds.INDEX_NAME);
    }

    /**
     * @param builtIn Sets builtIn to the specified value.
     */
    public void setBuiltIn( boolean builtIn ) {
        this.builtIn = builtIn;
    }

    /**
     * @return checksum
     */
    public long getChecksum() {
        return checksum;
    }

    /**
     * @param checksum Sets checksum to the specified value.
     */
    public void setChecksum( long checksum ) {
        this.checksum = checksum;
    }


    /**
     * @return the path in the VDB (can be <code>null</code> or empty)
     */
    public String getPathInVdb() {
        return pathInVdb;
    }

    /**
     * @return the paths of the imported models (never <code>null</code> but can be empty)
     */
    public Set<String> getImports() {
        return imports;
    }

    /**
     * @param newImport the model import path being added as an import (cannot be <code>null</code> or empty)
     */
    public void addImport( final String newImport ) {
        CheckArg.isNotEmpty(newImport, "newImport");
        this.imports.add(newImport);
    }

    /**
     * @return the sources of this models
     */
    public List<Source> getSources() {
        return this.sources;
    }

    /**
     * @param source a source of this model
     */
    public void addSource(Source source) {
        CheckArg.isNotNull(source, "source");
        this.sources.add(source);
    }

    /**
     * @return the validation markers (never <code>null</code>)
     */
    public List<ValidationMarker> getProblems() {
        return problems;
    }

    public void addProblem( Severity severity,
                            String path,
                            String message ) {
        problems.add(new ValidationMarker(severity, path, message));
    }

    public void addProblem( String severity,
                            String path,
                            String message ) {
        if (severity != null) {
            try {
                addProblem(Severity.valueOf(severity.trim().toUpperCase()), path, message);
            } catch (IllegalArgumentException e) {
                // Unknown severity, so ignore
            }
        }
    }

    /**
     * The natural order of VDB models is based upon dependencies (e.g., model imports), where models that depends upon other
     * models will always follow the models they depend on. Thus any model that has no dependencies will always appear first.
     * 
     * @see Comparable#compareTo(Object)
     */
    @Override
    public int compareTo( VdbModel that ) {
        if (that == null) return 1;
        if (that == this) return 0;
        if (this.getImports().contains(that.getPathInVdb())) {
            // this model imports that, so this model is greater than that ...
            return 1;
        }
        if (that.getImports().contains(this.getPathInVdb())) {
            // that model imports this, so this model is less than that ...
            return -1;
        }
        // Otherwise, neither model depends upon each other, so base the order upon the number of models ...
        return this.getImports().size() - that.getImports().size();
    }

    /**
     * A simple POJO that is used to represent the information for a model's source
     * read in from a VDB manifest ("vdb.xml").
     */
    public class Source {
        private String name;
        private String translator;
        private String jndiName;

        /**
         * Create a new source with the given name and translator
         *
         * @param name the source name (cannot be <code>null</code> or empty)
         * @param translator the source translator (can be <code>null</code>)
         */
        public Source(String name, String translator) {
            CheckArg.isNotEmpty(name, "name");
            CheckArg.isNotNull(translator, "translator");

            this.name = name;
            this.translator = translator;
        }

        /**
         * @return the name
         */
        public String getName() {
            return this.name;
        }

        /**
         * @return the translator
         */
        public String getTranslator() {
            return this.translator;
        }

        /**
         * @return the jndiName
         */
        public String getJndiName() {
            return this.jndiName;
        }

        /**
         * @param jndiName the jndiName to set
         */
        public void setJndiName(String jndiName) {
            this.jndiName = jndiName;
        }
    }

    /** The 'vdb.cnd' and 'teiid.cnd' files contain a property definition for 'vdb:severity' with these literal values. */
    public static enum Severity {
        WARNING,
        INFO,
        ERROR;
    }

    public static class ValidationMarker {
        private final String path;
        private final Severity severity;
        private final String message;

        public ValidationMarker( Severity severity,
                                 String path,
                                 String message ) {
            this.severity = severity != null ? severity : Severity.ERROR;
            this.path = path != null ? path : "";
            this.message = message != null ? message : "";
        }

        /**
         * @return message
         */
        public String getMessage() {
            return message;
        }

        /**
         * @return path
         */
        public String getPath() {
            return path;
        }

        /**
         * @return severity
         */
        public Severity getSeverity() {
            return severity;
        }

        /**
         * @see Object#toString()
         */
        @Override
        public String toString() {
            return severity.name() + " '" + path + "': " + message;
        }
    }
}
