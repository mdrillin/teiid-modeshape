/*
 * ModeShape (http://www.modeshape.org)
 * See the COPYRIGHT.txt file distributed with this work for information
 * regarding copyright ownership.  Some portions may be licensed
 * to Red Hat, Inc. under one or more contributor license agreements.
 * See the AUTHORS.txt file in the distribution for a full listing of 
 * individual contributors.
 *
 * ModeShape is free software. Unless otherwise indicated, all code in ModeShape
 * is licensed to you under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 * 
 * ModeShape is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.teiid.modeshape.sequencer.dataservice;

import java.util.Objects;

/**
 * Represents a Data Service service VDB in the archive.
 */
public class DataserviceServiceVdb {

    private String path;
    private final DataserviceManifest parent;

    public DataserviceServiceVdb( final DataserviceManifest manifest ) {
        this.parent = manifest;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals( final Object obj ) {
        if ( ( obj == null ) || !getClass().equals( obj.getClass() ) ) {
            return false;
        }

        final DataserviceServiceVdb that = ( DataserviceServiceVdb )obj;
        return Objects.equals( this.path, that.path );
    }

    public DataserviceManifest getParent() {
        return this.parent;
    }

    public String getPath() {
        return this.path;
    }

    /**
     * {@inheritDoc}
     *
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        return Objects.hash( this.path.hashCode() );
    }

    public void setPath( final String archivePath ) {
        this.path = archivePath;
    }

}