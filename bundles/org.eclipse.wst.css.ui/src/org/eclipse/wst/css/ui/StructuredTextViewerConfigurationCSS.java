/*****************************************************************************
 * Copyright (c) 2004 IBM Corporation and others. All rights reserved. This
 * program and the accompanying materials are made available under the terms
 * of the Eclipse Public License v1.0 which accompanies this distribution, and
 * is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: IBM Corporation - initial API and implementation
 ****************************************************************************/
package org.eclipse.wst.css.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.IAutoEditStrategy;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.formatter.IContentFormatter;
import org.eclipse.jface.text.formatter.MultiPassContentFormatter;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.wst.css.core.format.FormatProcessorCSS;
import org.eclipse.wst.css.core.internal.text.rules.StructuredTextPartitionerForCSS;
import org.eclipse.wst.css.ui.autoedit.StructuredAutoEditStrategyCSS;
import org.eclipse.wst.css.ui.contentassist.CSSContentAssistProcessor;
import org.eclipse.wst.css.ui.style.LineStyleProviderForCSS;
import org.eclipse.wst.css.ui.taginfo.CSSBestMatchHoverProcessor;
import org.eclipse.wst.sse.core.text.rules.StructuredTextPartitioner;
import org.eclipse.wst.sse.ui.EditorPlugin;
import org.eclipse.wst.sse.ui.StructuredTextViewerConfiguration;
import org.eclipse.wst.sse.ui.format.StructuredFormattingStrategy;
import org.eclipse.wst.sse.ui.style.IHighlighter;
import org.eclipse.wst.sse.ui.taginfo.AnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.ProblemAnnotationHoverProcessor;
import org.eclipse.wst.sse.ui.taginfo.TextHoverManager;
import org.eclipse.wst.sse.ui.util.EditorUtility;

public class StructuredTextViewerConfigurationCSS extends StructuredTextViewerConfiguration {

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.ibm.sse.editor.StructuredTextViewerConfiguration#getAutoEditStrategies(org.eclipse.jface.text.source.ISourceViewer)
	 */
	public Map getAutoEditStrategies(ISourceViewer sourceViewer) {
		Map result = super.getAutoEditStrategies(sourceViewer);

		if (result.get(StructuredTextPartitionerForCSS.ST_STYLE) == null)
			result.put(StructuredTextPartitionerForCSS.ST_STYLE, new ArrayList(1));

		IAutoEditStrategy autoEditStrategy = new StructuredAutoEditStrategyCSS();
		List strategies = (List) result.get(StructuredTextPartitionerForCSS.ST_STYLE);
		strategies.add(autoEditStrategy);

		return result;
	}

	public String[] getConfiguredContentTypes(ISourceViewer sourceViewer) {
		if (configuredContentTypes == null) {
			configuredContentTypes = new String[]{StructuredTextPartitionerForCSS.ST_STYLE, StructuredTextPartitioner.ST_DEFAULT_PARTITION, StructuredTextPartitioner.ST_UNKNOWN_PARTITION};
		}
		return configuredContentTypes;
	}

	public IContentAssistant getContentAssistant(ISourceViewer sourceViewer) {
		IContentAssistant contentAssistant = super.getContentAssistant(sourceViewer);

		if (contentAssistant != null && contentAssistant instanceof ContentAssistant) {
			//((ContentAssistant)
			// contentAssistant).setContentAssistProcessor(new
			// CSSContentAssistProcessor(),
			// StructuredTextPartitionerForCSS.ST_STYLE);
			IContentAssistProcessor cssProcessor = new CSSContentAssistProcessor();
			addContentAssistProcessor((ContentAssistant) contentAssistant, cssProcessor, StructuredTextPartitionerForCSS.ST_STYLE);
			addContentAssistProcessor((ContentAssistant) contentAssistant, cssProcessor, StructuredTextPartitioner.ST_UNKNOWN_PARTITION);
		}

		return contentAssistant;
	}

	public IContentFormatter getContentFormatter(ISourceViewer sourceViewer) {
		final MultiPassContentFormatter formatter = new MultiPassContentFormatter(getConfiguredDocumentPartitioning(sourceViewer), StructuredTextPartitionerForCSS.ST_STYLE);

		formatter.setMasterStrategy(new StructuredFormattingStrategy(new FormatProcessorCSS()));

		return formatter;
	}

	public IHighlighter getHighlighter(ISourceViewer sourceViewer) {
		IHighlighter highlighter = super.getHighlighter(sourceViewer);

		if (highlighter != null) {
			highlighter.addProvider(StructuredTextPartitionerForCSS.ST_STYLE, new LineStyleProviderForCSS());
		}

		return highlighter;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.text.source.SourceViewerConfiguration#getTextHover(org.eclipse.jface.text.source.ISourceViewer,
	 *      java.lang.String, int)
	 */
	public ITextHover getTextHover(ISourceViewer sourceViewer, String contentType, int stateMask) {
		// content type does not really matter since only combo, problem,
		// annotation hover is available
		TextHoverManager.TextHoverDescriptor[] hoverDescs = EditorPlugin.getDefault().getTextHoverManager().getTextHovers();
		int i = 0;
		while (i < hoverDescs.length) {
			if (hoverDescs[i].isEnabled() && EditorUtility.computeStateMask(hoverDescs[i].getModifierString()) == stateMask) {
				String hoverType = hoverDescs[i].getId();
				if (TextHoverManager.COMBINATION_HOVER.equalsIgnoreCase(hoverType))
					return new CSSBestMatchHoverProcessor();
				else if (TextHoverManager.PROBLEM_HOVER.equalsIgnoreCase(hoverType))
					return new ProblemAnnotationHoverProcessor();
				else if (TextHoverManager.ANNOTATION_HOVER.equalsIgnoreCase(hoverType))
					return new AnnotationHoverProcessor();
			}
			i++;
		}
		return super.getTextHover(sourceViewer, contentType, stateMask);
	}
}