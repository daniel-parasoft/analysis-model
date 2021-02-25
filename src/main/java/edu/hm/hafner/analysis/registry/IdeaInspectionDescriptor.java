package edu.hm.hafner.analysis.registry;

import edu.hm.hafner.analysis.parser.IdeaInspectionParser;

/**
 * A Descriptor for the Idea Inspection parser.
 *
 * @author Lorenz Munsch
 */
class IdeaInspectionDescriptor extends ParserDescriptor {
    private static final String ID = "idea";
    private static final String NAME = "IntelliJ IDEA Inspections";

    IdeaInspectionDescriptor() {
        super(ID, NAME, new IdeaInspectionParser());
    }
}
