/**********************************************************************
 * Copyright (c) 2005 IBM Corporation and others. All rights reserved.   This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: 
 * IBM - Initial API and implementation
 **********************************************************************/
package org.eclipse.wst.sse.core.internal;

import org.eclipse.osgi.util.NLS;

/**
 * Strings used by SSE Core
 * 
 * @since 1.0
 */
public class SSECoreMessages extends NLS {
	private static final String BUNDLE_NAME = "org.eclipse.wst.sse.core.internal.SSECorePluginResources";//$NON-NLS-1$

	public static String A_model_s_id_can_not_be_nu_EXC_;
	public static String Program_Error__ModelManage_EXC_;
	public static String Original_Error__UI_;
	public static String ModelPlugin_0;
	public static String StructuredDocumentBuilder_0;
	public static String Text_Change_UI_;

	public static String Migrate_Charset;

	static {
		// load message values from bundle file
		NLS.initializeMessages(BUNDLE_NAME, SSECoreMessages.class);
	}

	private SSECoreMessages() {
		// cannot create new instance
	}
}
