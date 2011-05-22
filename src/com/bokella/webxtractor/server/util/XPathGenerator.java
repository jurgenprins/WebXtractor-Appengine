package com.bokella.webxtractor.server.util;

import java.util.List;
import java.util.logging.Logger;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.bokella.webxtractor.server.services.web.DefaultWebPageService;

public class XPathGenerator {
	private static final Logger log = Logger.getLogger(DefaultWebPageService.class.getName());
	
	private static XPath xpath = XPathFactory.newInstance().newXPath();
	
	private static boolean isWhiteSpace(String nodeText) {
		if (nodeText.startsWith("\r") || nodeText.startsWith("\t")
				|| nodeText.startsWith("\n") || nodeText.startsWith(" "))
			return true;
		else
			return false;
	}

	/*
	 * Simple utility method to verify brutishly assembled xpath expressions
	 */
	private static boolean checkXPath(String xpathExpression, Node node) {
		try {
			Object xpathCheck;
			xpathCheck = xpath.evaluate(xpathExpression, node
					.getOwnerDocument(), XPathConstants.BOOLEAN);
			if (xpathCheck.toString() == "true") {
				return true;
			}
		} catch (XPathExpressionException xpe) {
		}
		return false;
	}

	/*
	 * Simple utility method to check for a text node on the currenrt XML
	 * element
	 */
	private static boolean hasValidText(Node node) {
		String textValue = node.getTextContent();

		return (textValue != null && textValue != ""
				&& isWhiteSpace(textValue) == false
				&& node.hasChildNodes());
	}

	/*
	 * Simple utility to check for attributes on the current element
	 */
	private static boolean hasValidAttributes(Node node) {
		return (node.getAttributes().getLength() > 0);

	}

	private static String buildAttribString(Node node) {
		NamedNodeMap nnlist = node.getAttributes();
		StringBuffer attribExpr = new StringBuffer("[");

		int attribCount = 0;
		// iterate over attributes
		for (int i = 0; i < nnlist.getLength(); i++) {
			// grab attribute name and value
			String attribName = nnlist.item(i).getNodeName();
			
			if (!attribName.contentEquals("width") &&
				!attribName.contentEquals("height") &&
				!attribName.contentEquals("src") &&
				!attribName.contentEquals("href")) {
				continue;
			}

			String attribValue = nnlist.item(i).getNodeValue();
			
			// if we've already added attributes to the path expression append
			// the current one
			if (attribCount > 0) {
				attribExpr.append(" and ");
			}

			attribExpr.append("@" + attribName + "=\"" + attribValue + "\"");
			attribCount++;
		}

		attribExpr.append("]");

		return attribExpr.toString();
	}

	/*
	 * processNode checks for attributes and text nodes on an xml node and
	 * process them accordingly
	 */
	public static String processNode(Node node, String parentXpathExpr) {
		StringBuffer pathExpr = new StringBuffer(parentXpathExpr + "/" + node.getNodeName());
		String tmpPathAppend = null;
			
		if (node.getNodeType() != Node.ELEMENT_NODE) {
			return null;
		}
		
		if (!node.getNodeName().contentEquals("a") &&
			!node.getNodeName().contentEquals("img")) {
			return pathExpr.toString();
		}
		
		if (hasValidAttributes(node) || hasValidText(node)) {
			if (hasValidAttributes(node)) {
				tmpPathAppend = buildAttribString(node);
				if (checkXPath(pathExpr.toString().concat(tmpPathAppend), node)) {
					pathExpr.append(tmpPathAppend);
				} 
			}

			if (hasValidText(node)) {
				Node textNode = node;

				while (textNode != null
					      && isWhiteSpace(textNode.getTextContent()) == true
					      && textNode.getNodeType() != Node.ELEMENT_NODE) {
					textNode = textNode.getFirstChild();
				}
				
				if (checkXPath(pathExpr.toString().concat(textNode.getTextContent()), node)) {
					pathExpr.append("[text()=\"" + textNode.getTextContent() + "\"]");
				} 
			}
		}

		return pathExpr.toString();
	}

	// This function takes a group of nodes and generates XPath expressions for
	// them until it runs out of nodes
	public static void processNodeList(NodeList nodelist, String parentXPathExpr, List<String> xpathExprs) {
		String nodeXPathExpr = null;
		for (int i = 0; i < nodelist.getLength(); i++) {
			parentXPathExpr = parentXPathExpr.replace("[text()=\"\"]", "");
			if ((nodeXPathExpr = processNode(nodelist.item(i), parentXPathExpr)) != null) {
				if (hasValidAttributes(nodelist.item(i)) || hasValidText(nodelist.item(i))) {
					xpathExprs.add(nodeXPathExpr);
				}
				processNodeList(nodelist.item(i).getChildNodes(), nodeXPathExpr, xpathExprs);
			} 
		}
	}
}

