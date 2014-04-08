package pt.utl.ist.repox.metadataTransformation;

import java.util.ArrayList;
import java.util.List;

public class TagGroup extends Tag {
	public static final String GROUP_DELIMITER_START = "group(";
	public static final String GROUP_DELIMITER_END = ")";
	public static final String TAG_SEPARATOR = "::";
	
	private List<Tag> tags;
	private List<String> tagPrefixes;
	private String initialPrefix;
	private String finalSuffix;
	private String commonXpath;

	public List<Tag> getTags() {
		return tags;
	}

	public void setTags(List<Tag> tags) {
		this.tags = tags;
	}

	public List<String> getTagPrefixes() {
		return tagPrefixes;
	}

	public void setTagPrefixes(List<String> tagPrefixes) {
		this.tagPrefixes = tagPrefixes;
	}

	public String getInitialPrefix() {
		return initialPrefix;
	}

	public void setInitialPrefix(String initialPrefix) {
		this.initialPrefix = initialPrefix;
	}

	public String getFinalSuffix() {
		return finalSuffix;
	}

	public void setFinalSuffix(String finalSuffix) {
		this.finalSuffix = finalSuffix;
	}
	
	public String getCommonXpath() {
		return commonXpath;
	}

	public void setCommonXpath(String commonXpath) {
		this.commonXpath = commonXpath;
	}

	@Override
	public String getXpath() {
		String fullTagString = GROUP_DELIMITER_START + "'" + (initialPrefix != null ? initialPrefix : "") + "'";
		fullTagString += TAG_SEPARATOR + (commonXpath != null ? commonXpath : "");
		
		for (int i = 0; i < tags.size(); i++) {
			if(i > 0) {
				fullTagString += TAG_SEPARATOR + "'" + (tagPrefixes != null ? tagPrefixes.get(i) : "") + "'";
			}
			fullTagString += TAG_SEPARATOR + tags.get(i).getXpath().trim();
		}
		
		fullTagString += TAG_SEPARATOR + "'" + (finalSuffix != null ? finalSuffix : "") + "'" + GROUP_DELIMITER_END;
		
		return fullTagString;
	}
	
	/**
	 * This constructor builds a TagGroup from the xpath virtual representation of the TagGroup.
	 * The xpath of a TagGroup must be inside group() and must have an initial prefix and final suffix,
	 * which may be empty strings. After the initial prefix pairs of prefix (must exist, but may be an
	 * empty string) and tag may be added.
	 * 
	 * <p>Ex: group('initialPrefix',/oneTag,'prefix',/anotherTag/subNode,'') </p>
	 *  
	 * @param stringRepresentation
	 */
	public TagGroup(String stringRepresentation) {
		this();
		String contentString = stringRepresentation.substring(GROUP_DELIMITER_START.length(), stringRepresentation.length() - 1);
		String[] splitTags = contentString.split(TAG_SEPARATOR);
		
		initialPrefix = splitTags[0].substring(1, splitTags[0].length() - 1);
		commonXpath = splitTags[1];
		finalSuffix = splitTags[splitTags.length - 1].substring(1, splitTags[splitTags.length - 1].length() - 1);
		
		tags = new ArrayList<Tag>();
		tagPrefixes = new ArrayList<String>();
		tags.add(new Tag("", "", splitTags[2]));
		tagPrefixes.add("");
		
		if(splitTags.length > 3) {
			int i = 3;
			while(i < splitTags.length - 1) {
				tagPrefixes.add(splitTags[i].substring(1, splitTags[i].length() - 1));
				i++;
				tags.add(new Tag("", "", splitTags[i]));
				i++;
			}
		}
	}
	
	public TagGroup() {
		super();
	}

	public TagGroup(List<Tag> tags, List<String> tagPrefixes, String initialPrefix, String finalSuffix) {
		this();
		this.tags = tags;
		this.tagPrefixes = tagPrefixes;
		this.initialPrefix = initialPrefix;
		this.finalSuffix = finalSuffix;
	}


}
