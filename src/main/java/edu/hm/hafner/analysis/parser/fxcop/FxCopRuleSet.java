package edu.hm.hafner.analysis.parser.fxcop;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Element;

import edu.hm.hafner.analysis.util.XmlElementUtil;
import edu.umd.cs.findbugs.annotations.CheckForNull;

/**
 * Internal set containing rules for FxCop.
 *
 * @author Erik Ramfelt
 */
@SuppressWarnings({"PMD", "all", "CheckStyle"})
public class FxCopRuleSet {
    private final Map<String, FxCopRule> rules = new HashMap<>();

    /***
     * Parse the element and insert the rule into the rule set.
     * @param element the element
     */
    public void addRule(final Element element) {
        var rule = new FxCopRule(element.getAttribute("TypeName"), element.getAttribute("Category"), element
                .getAttribute("CheckId"));
        rule.setUrl(getNamedTagText(element, "Url"));
        rule.setDescription(getNamedTagText(element, "Description"));
        rule.setName(getNamedTagText(element, "Name"));

        rules.put(getRuleKey(rule.getCategory(), rule.getCheckId()), rule);
    }

    /**
     * Returns the text value of the named child element if it exists
     *
     * @param element
     *         the element to check look for child elements
     * @param tagName
     *         the name of the child element
     *
     * @return the text value; or "" if no element was found
     */
    private String getNamedTagText(final Element element, final String tagName) {
        Optional<Element> foundElement = XmlElementUtil.getFirstChildElementByName(element, tagName);
        if (foundElement.isPresent()) {
            return foundElement.get().getTextContent();
        }
        else {
            return StringUtils.EMPTY;
        }
    }

    /**
     * Returns if the rule set contains a rule for the specified category and id
     *
     * @param category
     *         the rule category
     * @param checkId
     *         the rule id
     *
     * @return {@code true}  if the rule set contains a rule for the specified category and id, {@code false} otherwise
     */
    public boolean contains(final String category, final String checkId) {
        return rules.containsKey(getRuleKey(category, checkId));
    }

    /**
     * Returns the specified rule if it exists
     *
     * @param category
     *         the rule category
     * @param checkId
     *         the id of the rule
     *
     * @return the rule; null otherwise
     */
    @CheckForNull
    public FxCopRule getRule(final String category, final String checkId) {
        var key = getRuleKey(category, checkId);
        FxCopRule rule = null;
        if (rules.containsKey(key)) {
            rule = rules.get(key);
        }
        return rule;
    }

    /**
     * Returns the key for the map
     *
     * @param category
     *         category of the rule
     * @param checkId
     *         id of the rule
     *
     * @return category + "#" + checkid
     */
    private String getRuleKey(final String category, final String checkId) {
        return category + "#" + checkId;
    }
}
