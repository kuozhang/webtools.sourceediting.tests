/*******************************************************************************
 * Copyright (c) 2001, 2004 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Jens Lukowski/Innoopract - initial renaming/restructuring
 *     
 *******************************************************************************/
package org.eclipse.wst.sse.ui.extensions.spellcheck;

/**
 * @deprecated - to be removed in M4
 */

import org.eclipse.core.runtime.IStatus;

/**
 * SpellCheckException
 */
public abstract class SpellCheckException extends Exception {

	public SpellCheckException(String msg) {
		super(msg);
	}

	/**
	 * @return org.eclipse.core.runtime.IStatus
	 */
	public abstract IStatus getStatus();
}
