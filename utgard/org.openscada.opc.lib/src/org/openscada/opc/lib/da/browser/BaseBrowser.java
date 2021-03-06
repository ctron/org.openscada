/*
 * This file is part of the OpenSCADA project
 * Copyright (C) 2006-2010 TH4 SYSTEMS GmbH (http://th4-systems.com)
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */

package org.openscada.opc.lib.da.browser;

import java.net.UnknownHostException;
import java.util.Collection;
import java.util.EnumSet;

import org.jinterop.dcom.common.JIException;
import org.openscada.opc.dcom.common.impl.EnumString;
import org.openscada.opc.dcom.da.OPCBROWSETYPE;
import org.openscada.opc.dcom.da.impl.OPCBrowseServerAddressSpace;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A class implementing base browsing
 * @author Jens Reimann
 *
 */
public class BaseBrowser
{
    private static Logger _log = LoggerFactory.getLogger ( BaseBrowser.class );

    protected OPCBrowseServerAddressSpace _browser;

    /**
     * The batch size is the number of entries that will be requested with one call
     * from the server. Sometimes too big batch sizes will cause an exception. And
     * smaller batch sizes degrade performance. The default is set by {@link EnumString#DEFAULT_BATCH_SIZE}
     * and can be overridden by the java property {@code openscada.dcom.enum-batch-size}.
     */
    protected int _batchSize;

    public BaseBrowser ( final OPCBrowseServerAddressSpace browser )
    {
        this ( browser, EnumString.DEFAULT_BATCH_SIZE );
    }

    public BaseBrowser ( final OPCBrowseServerAddressSpace browser, final int batchSize )
    {
        super ();
        this._browser = browser;
        this._batchSize = batchSize;
    }

    /**
     * Set the batch size
     * @param batchSize The new batch size
     */
    public void setBatchSize ( final int batchSize )
    {
        this._batchSize = batchSize;
    }

    /**
     * Get the batch size
     * @return the current batch size
     */
    public int getBatchSize ()
    {
        return this._batchSize;
    }

    /**
     * Perform the browse operation.
     * @param type
     * @param filterCriteria
     * @param accessMask
     * @param variantType
     * @return The browse result
     * @throws IllegalArgumentException
     * @throws UnknownHostException
     * @throws JIException
     */
    protected Collection<String> browse ( final OPCBROWSETYPE type, final String filterCriteria, final EnumSet<Access> accessMask, final int variantType ) throws IllegalArgumentException, UnknownHostException, JIException
    {
        int accessMaskValue = 0;

        if ( accessMask.contains ( Access.READ ) )
        {
            accessMaskValue |= Access.READ.getCode ();
        }
        if ( accessMask.contains ( Access.WRITE ) )
        {
            accessMaskValue |= Access.WRITE.getCode ();
        }

        _log.debug ( "Browsing with a batch size of " + this._batchSize );

        return this._browser.browse ( type, filterCriteria, accessMaskValue, variantType ).asCollection ( this._batchSize );
    }

    /**
     * Browse the access paths for one item.
     * @param itemId The item ID to look up the access paths
     * @return The collection of the access paths
     * @throws JIException 
     * @throws UnknownHostException 
     * @throws IllegalArgumentException 
     */
    public Collection<String> getAccessPaths ( final String itemId ) throws IllegalArgumentException, UnknownHostException, JIException
    {
        return this._browser.browseAccessPaths ( itemId ).asCollection ( this._batchSize );
    }

}